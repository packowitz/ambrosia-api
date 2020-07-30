package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.buildings.merchant.MerchantPlayerItem
import io.pacworx.ambrosia.buildings.merchant.MerchantPlayerItemRepository
import io.pacworx.ambrosia.buildings.merchant.MerchantService
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.gear.Jewelry
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.VehicleService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("bazaar")
class BazaarController(
    val progressRepository: ProgressRepository,
    val resourcesService: ResourcesService,
    val merchantPlayerItemRepository: MerchantPlayerItemRepository,
    val heroService: HeroService,
    val gearRepository: GearRepository,
    val jewelryRepository: JewelryRepository,
    val vehicleService: VehicleService,
    val merchantService: MerchantService,
    val achievementsRepository: AchievementsRepository
) {

    @PostMapping("trade/{trade}")
    @Transactional
    fun trade(@ModelAttribute("player") player: Player, @PathVariable trade: Trade): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        if (!progress.tradingEnabled) {
            throw GeneralException(player, "Cannot trade", "Upgrade Bazaar to enable trading")
        }
        val resources = resourcesService.getResources(player)
        resourcesService.spendResource(resources, trade.giveType, trade.giveAmount - (progress.negotiationLevel * trade.negotiationGiveReduction))
        resourcesService.gainResources(resources, trade.getType, trade.getAmount + (progress.negotiationLevel * trade.negotiationGetIncrease))
        return PlayerActionResponse(resources = resources)
    }

    @GetMapping("merchant/items")
    @Transactional
    fun getMerchantItems(@ModelAttribute("player") player: Player): List<MerchantPlayerItem> {
        return merchantService.getItems(player)
    }

    @PostMapping("merchant/items/renew")
    @Transactional
    fun renewMerchantItems(@ModelAttribute("player") player: Player): PlayerActionResponse {
        val resources = resourcesService.getResources(player)
        val achievements = achievementsRepository.getOne(player.id)
        resourcesService.spendResource(resources, ResourceType.RUBIES, 50)
        achievements.resourceSpend(ResourceType.RUBIES, 50)
        val newItems = merchantService.getItems(player, forceRenew = true)
        return PlayerActionResponse(resources = resources, achievements = achievements, merchantItems = newItems)
    }

    @PostMapping("merchant/buy/{itemId}")
    @Transactional
    fun buyItem(@ModelAttribute("player") player: Player, @PathVariable itemId: Long): PlayerActionResponse {
        val item = merchantPlayerItemRepository.findByIdOrNull(itemId)
            ?: throw GeneralException(player, "Cannot buy item", "Item is not valid anymore")
        if (item.sold) {
            throw GeneralException(player, "Cannot buy item", "Item sold out")
        }
        val resources = resourcesService.getResources(player)
        val achievements = achievementsRepository.getOne(player.id)
        resourcesService.spendResource(resources, item.priceType, item.priceAmount)
        achievements.resourceSpend(item.priceType, item.priceAmount)
        achievements.merchantItemsBought ++
        item.sold = true
        return when {
            item.resourceType != null -> {
                resourcesService.gainResources(resources, item.resourceType!!, item.resourceAmount!!)
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.RESOURCE, resourceType = item.resourceType, value = item.resourceAmount?.toLong() ?: 0)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, looted = looted)
            }
            item.heroBaseId != null -> {
                val hero = heroService.recruitHero(player, item.heroBaseId!!, item.heroLevel!!)
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.HERO, value = hero.id)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, heroes = listOf(hero), looted = looted)
            }
            item.gearId != null -> {
                val gear = gearRepository.findByIdOrNull(item.gearId)
                    ?: throw EntityNotFoundException(player, "gear", item.gearId!!)
                gear.playerId = player.id
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.GEAR, value = gear.id)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, gears = listOf(gear), looted = looted)
            }
            item.jewelType != null -> {
                val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, item.jewelType!!)
                    ?: jewelryRepository.save(Jewelry(playerId = player.id, type = item.jewelType!!))
                jewelry.increaseAmount(item.jewelLevel!!, 1)
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.JEWEL, jewelType = item.jewelType, value = item.jewelLevel?.toLong() ?: 0)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, jewelries = listOf(jewelry), looted = looted)
            }
            item.vehicleBaseId != null -> {
                val vehicle = vehicleService.gainVehicle(player, item.vehicleBaseId!!)
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.VEHICLE, value = vehicle.id)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, vehicles = listOf(vehicle), looted = looted)
            }
            item.vehiclePartType != null -> {
                val vehiclePart = vehicleService.gainVehiclePart(player, item.vehiclePartType!!, item.vehiclePartQuality!!)
                val looted = Looted(LootedType.MERCHANT, listOf(
                    LootedItem(LootItemType.VEHICLE_PART, value = vehiclePart.id)
                ))
                PlayerActionResponse(resources = resources, achievements = achievements, boughtMerchantItem = item, vehicleParts = listOf(vehiclePart), looted = looted)
            }
            else -> throw ConfigurationException("Unknown item type to be bought")
        }
    }
}