// Visit the wiki for more info - https://kubejs.com/
ServerEvents.recipes(event => {
    event.recipes.modular_machinery_reborn.machine_recipe("mmr:testing", 15000)
        .requireEnergy(100000)
        .requireItem("2x modular_machinery_reborn:casing_plain")
        .produceFluid('10000x minecraft:lava')
})

MMREvents.machines(event => {
    event.create("mmr:testing")
        .color('#2291FFFF')
        .name('Testing machineJS')
        .structure(
            MMRStructureBuilder.create()
                .pattern(
                    [
                        ["rfr", "fpf", "rlr"],
                        ["rmr", "o i", "rer"],
                        ["rfr", "fpf", "rfr"]
                    ]
                )
                .keys(
                    {
                        "r": "modular_machinery_reborn:casing_reinforced",
                        "f": "modular_machinery_reborn:casing_firebox",
                        "p": "modular_machinery_reborn:casing_plain",
                        "e": "#modular_machinery_reborn:energyinputhatch",
                        "o": "#modular_machinery_reborn:outputbus",
                        "i": "#modular_machinery_reborn:inputbus",
                        "l": "#modular_machinery_reborn:fluidoutputhatch"
                    }
                )
        )
})

