# ColorsApp
This is my first Android App, so through this app, I'll be exploring different aspects of Android development. 
My main goal for this app is to learn the "ins and outs" of Android development through research and through the official Android documentation. 
For this project, I went with Java because it is what I know best. 

I want to eventually convert it to a Kotlin application but first I need to learn Kotlin. I figured that it is best to learn and understand the basic of Android development first and foremost. Then learn and convert to it a Kotlin application. 

The basic functionality is when you tap on the screen, you'll get a random color. This will then display the hex and rgb values of the current color. Clicking on the hex value will take you to the value's <a href="https://encycolorpedia.com/">encycolorpedia</a> page. You can also hide this information if you just want to see the color.<br>
<b>DISCLAIMER: I do not have any affiliation with Encycolorpedia and their content. I just thought their site is pretty cool and served as a resource for more detailed color information.</b>

If you want to fine tune the color, sliders are provided with the ability to change the Hue, Saturation and Brightness values.<br>
This was done by generating seprate Hue, Saturation and Brightness values to create the color. The sliders then allow modification of each individual value. More information on how HSL and HSV/HSB works <a href="https://en.wikipedia.org/wiki/HSL_and_HSV">here</a>. <br>

I used the <a href="https://stackoverflow.com/questions/18141976/how-to-invert-an-rgb-color-in-integer-form/18142036">inverted/comp color</a> of the current background color in order to make this information viewable without giving each display field a background. This doesn't work for a few cases so it will be changed later on for now, it's good enough. 
