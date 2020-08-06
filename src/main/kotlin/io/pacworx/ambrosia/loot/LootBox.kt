package io.pacworx.ambrosia.loot

import javax.persistence.*

@Entity
data class LootBox(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: LootBoxType
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "loot_box_id")
    @OrderBy("slot_number ASC, item_order ASC")
    var items: List<LootItem> = ArrayList()
}
