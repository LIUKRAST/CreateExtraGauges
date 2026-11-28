Features
- Works together with Create: Factory Logistics 1.6.1 or newer, including String Gauge address rewriting on packagers ([#112](https://github.com/LIUKRAST/CreateExtraGauges/issues/112))
- ValueEditBox implementation
- Passive Gauges open the item selection screen like factory gauges, and show "New factory task" while no item is set
- Block entity type alias `linked_lever` for `linked_control` ([#122](https://github.com/LIUKRAST/CreateExtraGauges/issues/122))

Performance
- Logic, String, Counter and Comparator Gauges no longer notify their outputs twice for the same change ([#89](https://github.com/LIUKRAST/CreateExtraGauges/issues/89))
- String Gauges cache their regular expressions, Expression Gauges reuse the compiled expression and number format

Fixes
- Expression and String Gauges cut off their initial value
- Gauges don't save their settings when saved into schematics ([#123](https://github.com/LIUKRAST/CreateExtraGauges/issues/123)). Connections between gauges are still not kept by schematics
- String Gauges with an invalid replacement show an error instead of crashing
- Filter Gauge required 64 times the batch amount before switching item when the target gauge counted in stacks ([#126](https://github.com/LIUKRAST/CreateExtraGauges/issues/126))
- Filter Gauge crashed the server when switching an ingredient of a recipe loaded from the world
- Display Collectors linked to brass tunnels were forgotten after the world was saved and loaded again
- Passive Gauge tooltip
- Linked Button tooltip said it was a lever
- Shift summaries of Linked Lever, Linked Button, Redstone Port and Rose Quartz Port were missing

Other changes
- Requires Deployer 1.0.0
- Clearer Ponder texts; String Gauge, Filter Gauge and Display Collector are listed under High Logistics
- New Traditional Chinese localisation
- Updated Chinese (simplified) and Italian localisation
