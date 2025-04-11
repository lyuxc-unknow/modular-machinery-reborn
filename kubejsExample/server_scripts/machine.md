# Machine
___
### Of
```js
let machine = MachineController.of(blockEntity)
let machine = MachineController.of(blockContainer)
```
___
### Id
```js
// Get the machine id
let id = event.machine.id

// Change the machine id
event.machine.id = "namespace:id"
```
Remainder: Must be a machine neither created with KubeJS nor located in `data/namespace/machines/`
will have the id: `namespace:path_to_machine_file_json/machine_json_name` or the id putted in the kubejs event
___
### Pause
```js
let machine = event.machine
// Return true if the machine is paused, false otherwise
let paused = machine.paused

// Pause the machine
machine.paused = true
// Resume the machine
machine.paused = false 
```
___
### Energy
```js
let machine = event.machine
let energy = machine.getEnergyStored(IOType.INPUT || IOType.OUTPUT)
let energyCapacity = machine.getEnergyCapacity(IOType.INPUT || IOType.OUTPUT)

// add to energy output hatches
let addedEnergy = machine.addEnergy(amount)
// remove from energy input hatches
let removedEnergy = machine.removeEnergy(amount)
```
___
### Fluids
```js
let machine = event.machine
let fluids = machine.getFluidsStored(IOType.INPUT || IOType.OUTPUT) // returns List<FluidStack>
let capcity = machine.getFluidCapacity(IOType.INPUT || IOType.OUTPUT) // return the sum of every fluid hatch by IO mode
// The given fluid must be a KubeJS FluidStackJS
// Returns the sum of every fluid hatch by IO mode that contains the given fluid
let fluidCapacity = machine.getFluidCapacity(fluid, IOType.INPUT || IOType.OUTPUT)
// Add the specified fluid to the first available fluid output hatch
// return the amount of fluid that was added.
// The passed fluid must be a KubeJS FluidStackJS
// It will always be really inserted
let added = machine.addFluid(fluid)

// Extracts the specified fluid from the first available fluid input hatch
// return the amount of fluid that was extracted.
// The passed fluid must be a KubeJS FluidStackJS
// It will always be really extracted
let removed = machine.removeFluid(fluid)
```
___
### Items
```js
let machine = event.machine
// Return a list of stored items in the given hatch mode
let items = machine.getItemsStored(IOType.INPUT || IOType.OUTPUT)

// Add the specified item to the first available item output bus
// Return the item that couldn't be added, or an empty ItemStack if all items were successfully added.
// The passed item must be a KubeJS ItemStackJS
// It will always be really inserted
let remaining = machine.addItem(item)

//Remove the specified amount of item from the specified slot.
//Return the item that were successfully removed, or an empty ItemStack if the slot was empty.
//The returned item is a KubeJS ItemStackJS, see docs : https://kubejs.com/wiki/kubejs/ItemStackJS/
// The passed item must be a KubeJS ItemStackJS
// It will always be really extracted
var extracted = machine.removeItem(item);

```
___
### Chunkload
```js
// THIS WILL ONLY WORK IF EXISTS A CHUNKLOADER BLOCK IN THE VALIDATED STRUCTURE
let machine = event.machine
// Enable chunkloading with a radius of 1
machine.enableChunkloading(1)
// Disable chunkloading
machine.disableChunkloading()

// Check if chunkloading is active
let active = machine.isChunkloadEnabled()
// Get the current chunkload radius
let radius = machine.getChunkloadRadius()
```
___