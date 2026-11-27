Features
- ValueEditBox implementation

Fixes
- Expression and String Gauges cut off their initial value
- Gauges don't save their NBT-Data when saved into schematics
- Filter Gauge required 64 times the batch amount before switching item when the target gauge counted in stacks ([#126](https://github.com/LIUKRAST/CreateExtraGauges/issues/126))
- Filter Gauge crashed the server when switching an ingredient of a recipe loaded from the world
- Display Collectors linked to brass tunnels were forgotten after the world was saved and loaded again
- The game no longer fails to start when Create Factory Logistics is installed. With it, the String Gauge cannot rewrite packager addresses yet