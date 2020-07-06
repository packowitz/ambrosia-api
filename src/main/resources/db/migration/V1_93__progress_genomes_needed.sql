alter table progress add column if not exists simple_genomes_needed integer not null default 0;
alter table progress add column if not exists common_genomes_needed integer not null default 0;
alter table progress add column if not exists uncommon_genomes_needed integer not null default 0;
alter table progress add column if not exists rare_genomes_needed integer not null default 0;
alter table progress add column if not exists epic_genomes_needed integer not null default 0;

delete from dynamic_property where type in ('SIMPLE_GENOME_COST', 'COMMON_GENOME_COST', 'UNCOMMON_GENOME_COST', 'RARE_GENOME_COST', 'EPIC_GENOME_COST');
delete from property_version where property_type in ('SIMPLE_GENOME_COST', 'COMMON_GENOME_COST', 'UNCOMMON_GENOME_COST', 'RARE_GENOME_COST', 'EPIC_GENOME_COST');

insert into dynamic_property (category, type, level, progress_stat, value1, version) values
('BUILDING', 'LABORATORY_BUILDING', 1, 'SIMPLE_GENOMES_NEEDED', 60, 1),
('BUILDING', 'LABORATORY_BUILDING', 1, 'COMMON_GENOMES_NEEDED', 60, 1),
('BUILDING', 'LABORATORY_BUILDING', 1, 'UNCOMMON_GENOMES_NEEDED', 60, 1),
('BUILDING', 'LABORATORY_BUILDING', 1, 'RARE_GENOMES_NEEDED', 60, 1),
('BUILDING', 'LABORATORY_BUILDING', 1, 'EPIC_GENOMES_NEEDED', 60, 1),
('BUILDING', 'LABORATORY_BUILDING', 4, 'SIMPLE_GENOMES_NEEDED', -5, 1),
('BUILDING', 'LABORATORY_BUILDING', 6, 'COMMON_GENOMES_NEEDED', -5, 1),
('BUILDING', 'LABORATORY_BUILDING', 8, 'SIMPLE_GENOMES_NEEDED', -5, 1),
('BUILDING', 'LABORATORY_BUILDING', 10, 'COMMON_GENOMES_NEEDED', -5, 1);

update progress set simple_genomes_needed = 60, common_genomes_needed = 60, uncommon_genomes_needed = 60, rare_genomes_needed = 60, epic_genomes_needed = 60 where exists(select 1 from building where building.player_id = progress.player_id and building.type = 'LABORATORY');
