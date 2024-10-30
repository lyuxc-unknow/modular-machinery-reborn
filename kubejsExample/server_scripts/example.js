// Visit the wiki for more info - https://kubejs.com/
ServerEvents.recipes(event => {
    event.recipes.modular_machinery_reborn.machine_recipe("mmr:testing", 150)
        .requireEnergy(100000)
        .requireItem("2x modular_machinery_reborn:casing_plain")
        .produceItem('1x modular_machinery_reborn:modularium')
        .produceFluid('10000x minecraft:lava')
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
                        ["rfr", "fpf", "rfr"]
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
                            "modular_machinery_reborn:inputbus_tiny",
                            "modular_machinery_reborn:inputbus_small",
                            "modular_machinery_reborn:inputbus_normal",
                            "modular_machinery_reborn:inputbus_reinforced",
                            "modular_machinery_reborn:inputbus_big",
                            "modular_machinery_reborn:inputbus_huge",
                            "modular_machinery_reborn:inputbus_ludicrous",
                            "modular_machinery_reborn:fluidoutputhatch_tiny",
                            "modular_machinery_reborn:fluidoutputhatch_small",
                            "modular_machinery_reborn:fluidoutputhatch_normal",
                            "modular_machinery_reborn:fluidoutputhatch_reinforced",
                            "modular_machinery_reborn:fluidoutputhatch_big",
                            "modular_machinery_reborn:fluidoutputhatch_huge",
                            "modular_machinery_reborn:fluidoutputhatch_ludicrous",
                            "modular_machinery_reborn:fluidoutputhatch_vacuum",
                            "modular_machinery_reborn:outputbus_tiny",
                            "modular_machinery_reborn:outputbus_small",
                            "modular_machinery_reborn:outputbus_normal",
                            "modular_machinery_reborn:outputbus_reinforced",
                            "modular_machinery_reborn:outputbus_big",
                            "modular_machinery_reborn:outputbus_huge",
                            "modular_machinery_reborn:outputbus_ludicrous"
                        ]
                    }
                )
        )
})

