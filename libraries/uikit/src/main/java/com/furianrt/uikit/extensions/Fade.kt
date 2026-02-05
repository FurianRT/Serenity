package com.furianrt.uikit.extensions

import android.graphics.RuntimeShader
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import org.intellij.lang.annotations.Language

@Language(value = "AGSL")
private val bottomFadingEdgeShader = """
    const half4 BLACK_COLOR = half4(0, 0, 0, 1);
    uniform float2 resolution;
    uniform float bottomFade;
    half4 main(float2 coord) {
        if (bottomFade < 1) {
            return BLACK_COLOR; // no fading needed at all
        } else if (coord.y < resolution.y - bottomFade) {
            return BLACK_COLOR; // no fading needed outside of the fading edge area
        } else {
            // formula: y = ((1 - x)^3 + 3(1 - x)^2 * x)^2
            float x = ((resolution.y - coord.y) / bottomFade); // x is 0 at the BOTTOM
            float y = (1.0 - x) * (1.0 - x) * (1.0 - x) + 3.0 * (1.0 - x) * (1.0 - x) * x;
            float alpha = 1.0 - y * y; 
            return half4(0, 0, 0, alpha); // return black color with the calculated alpha
        }
    }
"""

fun Modifier.fadingBottomEdge() = then(
    Modifier
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithCache {
            val bottomEdgeShader = RuntimeShader(bottomFadingEdgeShader)
            bottomEdgeShader.setFloatUniform("resolution", size.width, size.height)
            val bottomEdgeBrush = ShaderBrush(bottomEdgeShader)
            onDrawWithContent {
                drawContent()
                bottomEdgeShader.setFloatUniform("bottomFade", size.height * 0.8f)
                drawRect(
                    brush = bottomEdgeBrush,
                    blendMode = BlendMode.DstIn,
                )
            }
        }
)