// Visit the wiki for more info - https://kubejs.com/
ServerEvents.recipes(event => {
    event.recipes.modular_machinery_reborn.machine_recipe("mmr:testing", 150)
        // OPTIONAL CUSTOMIZATION {
        .progressX(number) // default 74
        .progressY(number) // default 8
        .width(number) // default 256
        .height(number) // default 256
        // }
        .requireEnergy(100000, x, y)
        .produceEnergy(100, x, y)
        .requireItem("2x modular_machinery_reborn:casing_plain", x, y) // valid for item tags too(uses sized ingredient)
        .requireItem("2x modular_machinery_reborn:casing_plain", 0.5, x, y) // valid for item tags too(uses sized ingredient)
        .produceItem('1x modular_machinery_reborn:modularium', x, y)
        .produceItem('1x modular_machinery_reborn:modularium', 0.8, x, y)
        .produceFluid('10000x minecraft:lava', x, y)
        .produceFluid('10000x minecraft:lava', 0.75, x, y)
        .requireFluid('minecraft:lava', x, y)
        .requireFluid('minecraft:lava', 0.15, x, y)
        // list of dimensions, blacklist
        .dimensions(['minecraft:overworld'], true, x, y)
        // default blacklist: false
        .dimensions(['minecraft:overworld'], x, y)
        // list of biomes, blacklist
        .biomes(['minecraft:plains'], true, x, y)
        // default blacklist: false
        .biomes(['minecraft:plains'], x, y)
        // weather time: rain, clear, snow, thunder
        .weather('clear', x, y)
        // time
        .time('[0,24000]', x, y)
        // chunkload
        .chunkload(3, x, y)
        // loottable
        .lootTable('minecraft:chests/ancient_city') // luck: 0, x: 0, y: 0
        .lootTable('minecraft:chests/ancient_city', luck) // x: 0, y: 0
        .lootTable('minecraft:chests/ancient_city', x, y) // luck: 0
        .lootTable('minecraft:chests/ancient_city', luck, x, y)
        // Experience
        .produceExperience(100)
        .requireExperience(100)
        // only if mekanism addon available
        .produceChemical('10x mekanism:sulfuric_acid', x, y)
        .requireChemical('10x mekanism:sulfuric_acid', x, y)
        // only if ars addon available
        .produceSource(100, x, y)
        .requireSource(100, x, y)

    /**
     * Width/Height and positions in pixels
     *
     * Progress width: 22
     * Progress height: 16
     *
     * Energy bar width: internal 16, external 18,
     * Energy bar height: internal 52, external 54
     *
     * Item slot/loot table slot(cycles between every possible item) width/height: internal 16, external 18
     *
     * Fluid tank width/height: internal 16, external 18
     *
     * Dimension, Biome, Time, Weather, Chunkload width/height: 18
     *
     * Addons:
     * Chemical tank width/height: internal 16, external 18
     *
     * Source buffer width/height: internal 14, external 18
     */
})

MMREvents.machines(event => {
    /**
     * Creates a machine with the given ResourceLocation (namespace:machine) equivalent to the json -> datapackNamespace:jsonName
     */
    event.create("mmr:testing")
        /**
         * Applies the color to the machine with the format:
         * #AlphaAlphaRedRedGreenGreenBlueBlue
         * color in int number format
         */
        .color('#FF2291FF')
        /**
         * The name that the machine should display in the controller item and in the controller GUI -> default localized with the id of the creation
         */
        .name('Testing machineJS')
        /**
         * The multiblock definition
         */
        .structure(
            /**
             * Creates the structure builder
             */
            MMRStructureBuilder.create()
                /**
                 * Sets the patten (bottom to top and front to back)
                 * Must have ONLY one `m` representing the controller block
                 */
                .pattern(
                    [
                        ["rfr", "fpf", "rir"],
                        ["rmr", "i i", "rer"],
                        ["rfr", "fpf", "rir"]
                    ]
                )
                /**
                 * Sets the block/blockstate, block tag or array of block/blockstates for the key defined in the patten
                 * if any key defined in pattern is missing here (except `m` of machine) the machine will not be loaded and an error is thrown
                 * if any key defined below is not present in the pattern it will be ignored
                 *
                 * The available tags from mmr are:
                 *
                 * #modular_machinery_reborn:all_casing -> includes every tag defined below
                 * #modular_machinery_reborn:casing
                 * #modular_machinery_reborn:energyhatch
                 * #modular_machinery_reborn:energyinputhatch
                 * #modular_machinery_reborn:energyoutputhatch
                 * #modular_machinery_reborn:fluidhatch -> includes input and output tags for fluids(ae2 included)
                 * #modular_machinery_reborn:fluidinputhatch
                 * #modular_machinery_reborn:fluidoutputhatch
                 * #modular_machinery_reborn:itembus -> includes input and output tags for items(ae2 included)
                 * #modular_machinery_reborn:inputbus
                 * #modular_machinery_reborn:outputbus
                 * #modular_machinery_reborn:experiencehatch -> includes input and output tags for experience(ae2 included)
                 * #modular_machinery_reborn:experienceinputhatch
                 * #modular_machinery_reborn:experienceoutputhatch
                 *
                 * Only if mekanism addon available
                 * #modular_machinery_reborn_mekanism:chemicalhatch -> includes input and output tags for chemicals(ae2 included)
                 * #modular_machinery_reborn_mekanism:chemicalinputhatch
                 * #modular_machinery_reborn_mekanism:chemicaloutputhatch
                 * Only if mekanism and ae2 addon available
                 * #modular_machinery_reborn_energistics:me_chemical_hatch
                 * #modular_machinery_reborn_energistics:me_chemical_inputhatch
                 * #modular_machinery_reborn_energistics:me_chemical_outputhatch
                 *
                 * Only if ars addon available
                 * #modular_machinery_reborn_ars:sourcehatch -> includes input and output tags for source(ae2 included)
                 * #modular_machinery_reborn_ars:sourceinputhatch
                 * #modular_machinery_reborn_ars:sourceputputhatch
                 * Only if ars and ae2 addon available
                 * #modular_machinery_reborn_energistics:me_source_hatch
                 * #modular_machinery_reborn_energistics:me_source_inputhatch
                 * #modular_machinery_reborn_energistics:me_source_outputhatch
                 *
                 * Only if ae2 addon available
                 * #modular_machinery_reborn_energistics:me_itembus
                 * #modular_machinery_reborn_energistics:me_iteminputbus
                 * #modular_machinery_reborn_energistics:me_itemoutputbus
                 * #modular_machinery_reborn_energistics:me_fluidhatch
                 * #modular_machinery_reborn_energistics:me_fluidinputhatch
                 * #modular_machinery_reborn_energistics:me_fluidoutputhatch
                 * #modular_machinery_reborn_energistics:me_experiencehatch
                 * #modular_machinery_reborn_energistics:me_experienceinputhatch
                 * #modular_machinery_reborn_energistics:me_experienceoutputhatch
                 */
                .keys(
                    {
                        /**
                         * Exact block
                         */
                        "r": "modular_machinery_reborn:casing_reinforced",
                        "f": "modular_machinery_reborn:casing_firebox",
                        "p": "modular_machinery_reborn:casing_plain",
                        /**
                         * Blocks defined in the tag
                         */
                        "e": "#modular_machinery_reborn:energyinputhatch",
                        /**
                         * Exact blocks that can be in that position
                         */
                        "i": [
                            "#modular_machinery_reborn:inputbus",
                            "#modular_machinery_reborn:fluidoutputhatch",
                            "#modular_machinery_reborn_mekanism:chemicaloutputhatch",
                            "#modular_machinery_reborn:outputbus",
                            "modular_machinery_reborn:biome_reader",
                            "modular_machinery_reborn:dimensional_detector",
                            "modular_machinery_reborn:weather_sensor",
                            "modular_machinery_reborn:time_counter",
                            "modular_machinery_reborn:chunkloader"
                        ]
                    }
                )
        )
        .controllerModel(ControllerModel.of("<namespace>:<path under assets/models/(controller|controllers)>")) // Ex: minecraft:furnace, mekanism:chemical_infuser, etc

    // To create an empty machine (just the controller to make the multiblocks easier with the structure builder tool
    event.create("mmr:empty")
})

