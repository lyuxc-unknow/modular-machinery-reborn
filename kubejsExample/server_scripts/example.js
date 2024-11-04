// Visit the wiki for more info - https://kubejs.com/
ServerEvents.recipes(event => {
    event.recipes.modular_machinery_reborn.machine_recipe("mmr:testing", 150)
        .requireEnergy(100000)
        .produceEnergy(100)
        .requireItem("2x modular_machinery_reborn:casing_plain")
        .requireItemTag('<item tag>', amount)
        // default amount is 1
        .requireItemTag('<item tag>')
        .produceItem('1x modular_machinery_reborn:modularium')
        .produceFluid('10000x minecraft:lava')
        .requireFluid('minecraft:lava')
        // list of dimensions, blacklist
        .dimensions(['minecraft:overworld'], true)
        // default blacklist: false
        .dimensions(['minecraft:overworld'])
        // list of biomes, blacklist
        .biomes(['minecraft:plains'], true)
        // default blacklist: false
        .biomes(['minecraft:plains'])
        // weather time: rain, clear, snow, thunder
        .weather('clear')
        // time
        .time('[0,24000]')
        // chunkload
        .chunkload(3)
        // only if mekanism addon available
        .produceChemical('10x mekanism:sulfuric_acid')
        .requireChemical('10x mekanism:sulfuric_acid')
        // only if ars addon available
        .produceSource(100)
        .requireSource(100)
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
         * The name that the machine should display in the controller item and in the controller GUI
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
                 * #modular_machinery_reborn:all_casing
                 * #modular_machinery_reborn:casing
                 * #modular_machinery_reborn:energyinputhatch
                 * #modular_machinery_reborn:energyoutputhatch
                 * #modular_machinery_reborn:fluidinputhatch
                 * #modular_machinery_reborn:fluidoutputhatch
                 * #modular_machinery_reborn:inputbus
                 * #modular_machinery_reborn:outputbus
                 *
                 * Only if mekanism addon available
                 * #modular_machinery_reborn_mekanism:chemicalinputhatch
                 * #modular_machinery_reborn_mekanism:chemicaloutputhatchç
                 *
                 * Only if ars addon available
                 * #modular_machinery_reborn_ars:sourceinputhatch
                 * #modular_machinery_reborn_ars:sourceputputhatch
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
})

