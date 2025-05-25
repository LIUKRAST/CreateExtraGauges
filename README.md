<p align="center">
  <img src="https://raw.githubusercontent.com/LIUKRAST/CreateExtraGauges/refs/heads/master/page/title.png"  alt="Title"/>
</p>

**This mod adds new custom gauges to create mod, like the factory gauge, but with different functions**

It also adds a custom API which allows mod developers to make their own gauges
(See more in the [Wiki](https://github.com/LIUKRAST/CreateExtraGauges/wiki)).

## ![Logic Gauge](page/logic_gauge.png) Logic Gauge
> The logic gauge allows transferring redstone data through their connections.
> Instead of having an item filter like the factory gauge,
> it lets you select between many Logic Gates (OR, AND, NAND, NOR, XOR, XNOR)
> and will use these gates to manage the inputs/outputs.<br>
> `Example: You have two redstone signals coming into the gauge, and your logic gate is set to "AND",
> it will only output redstone if ALL the inputs are on.`

## ![Integer Gauge](page/integer_gauge.png) Integer Gauge
> The integer gauge transfers integer (numerical) data through their connections.
> Instead of a logic gate, it will have some mathematical operation gates (+, -, *),
> and will apply those operations with the inputs.
> If connected to **Factory Gauges**,
> they will send the item amount of that type in the linked storage all the way to the integer gauge.<br>
> `Example: You have to factory gauge inputs (one for diamonds and one for emeralds), and you have 3 diamonds and 2 emeralds in your storage.
> You can use the integer gauge to sum, subtract or multiply these two numbers to get an integer output (for example 3 + 2 = 5). This can be directly sent to redstone links or logic gauges converting the number into a redstone signal.<br>
> NOTE: The subtract operation isn't really a subtract operation. Instead, it's a sum that gets then inverted (positive -> negative and viceversa)`

## ![Comparator Gauge](page/comparator_gauge.png) Comparator Gauge
> The comparator gauge converts integer data into redstone data, using a specific comparison operation.
> By holding right-click, you can select a **Value** which will be stored into your gauge data.
> By just right-clicking (without holding),
> you can instead open the menu to set up the comparison mode (=, !=, >, <, >=, <=).
> The comparison mode will then be used to evaluate the result based on the input and the value.
> Let's say you input a value of 15, and the mode is =, it will be compared `input = value`, and if it's true,
> it will output a redstone signal

## ![Counter Gauge](page/counter_gauge.png) Counter Gauge
> The counter-gauge can convert redstone inputs in both integer and another redstone data,
> let's see.
> Each time a redstone pulse is sent to the gauge, the counter will increase by one.
> By holding right-click, you can change the threshold,
> which will define the maximum number your count can reach.
> Once the threshold is reached, redstone output will be sent.
> Another redstone pulse will reset the counter to 0.


**Thanks to Propants05 for the initial mod idea, and Professaurus for supporting the project through donations.**
<p align="center">
<a href="https://discord.gg/pvn8zg9bNY"><img src="http://play.liukrast.net/discord.png" onmouseover="this.src='http://play.liukrast.net/discord_hovered.png'" onmouseout="this.src='http://play.liukrast.net/discord.png'" width="160" style="image-rendering: pixelated"/></a>
<a href="https://github.com/Creators-of-Create/Create/issues"><img src="http://play.liukrast.net/modrinth.png" onmouseover="this.src='http://play.liukrast.net/modrinth_hovered.png'" onmouseout="this.src='http://play.liukrast.net/modrinth.png'" width="160" style="image-rendering: pixelated"/></a>
<a href="https://www.youtube.com/channel/UCrKV2QTuyGcv4E3eSJpBiYA/playlists"><img src="http://play.liukrast.net/curseforge.png" onmouseover="this.src='http://play.liukrast.net/curseforge_hovered.png'" onmouseout="this.src='http://play.liukrast.net/curseforge.png'" width="160" style="image-rendering: pixelated"/></a>
</p>
