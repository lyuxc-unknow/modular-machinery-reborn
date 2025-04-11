# Function Event
___
### Get the remaining crafting time
```js
MMREvents.recipeFunction("id", event => {
  let time = event.remainingTime
})
```
___
### Get the crafting process base speed
```js
MMREvents.recipeFunction("id", event => {
  let speed = event.baseSpeed
})
```
___
### Set the crafting process base speed
```js
MMREvents.recipeFunction("id", event => {
  event.baseSpeed = 2
})
```
___
### Get the crafting process modified speed
```js
MMREvents.recipeFunction("id", event => {
  let speed = event.modifiedSpeedM
})
```
___
### Get the controller as a tile entity
```js
MMREvents.recipeFunction("id", event => {
  let tile = event.tile
})
```
___
### Get the controller as a machine
```js
MMREvents.recipeFunction("id", event => {
  let machine = event.machine
})
```
See the machine content [here](./machine.md)
___
### Get the controller as a BlockContainerJS
```js
MMREvents.recipeFunction("id", event => {
  let block = event.block
})
```
