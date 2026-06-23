package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.Accessory
import com.example.ui.PetState
import com.example.ui.theme.GptAccent
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RobotPet(
    state: PetState,
    battery: Int,
    accessory: Accessory = Accessory.NONE,
    modifier: Modifier = Modifier,
    onDoubleTap: () -> Unit,
    onStateChangeRequest: (PetState) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Breathing animation
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Floating animation
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Hand/Arm animation targets
    val normRightElbowTarget = when(state) {
        PetState.Thinking -> Offset(1.2f, 1.0f)
        PetState.Celebrating -> Offset(1.4f, -0.2f)
        PetState.Worried -> Offset(1.1f, 0.5f)
        else -> Offset(1.2f, 0.8f)
    }
    val normRightHandTarget = when(state) {
        PetState.Thinking -> Offset(0.3f, 0.3f) // Towards chin
        PetState.Celebrating -> Offset(1.5f, -1.2f)
        PetState.Worried -> Offset(0.5f, 0.1f)
        else -> Offset(1.1f, 1.4f)
    }

    val normLeftElbowTarget = when(state) {
        PetState.Thinking -> Offset(-1.2f, 0.8f)
        PetState.Celebrating -> Offset(-1.4f, -0.2f)
        PetState.Worried -> Offset(-1.1f, 0.5f)
        else -> Offset(-1.2f, 0.8f)
    }
    val normLeftHandTarget = when(state) {
        PetState.Thinking -> Offset(-1.0f, 1.2f)
        PetState.Celebrating -> Offset(-1.5f, -1.2f)
        PetState.Worried -> Offset(-0.5f, 0.1f)
        else -> Offset(-1.1f, 1.4f)
    }

    val normRightElbow by animateOffsetAsState(normRightElbowTarget, tween(500))
    val normRightHand by animateOffsetAsState(normRightHandTarget, tween(500))
    val normLeftElbow by animateOffsetAsState(normLeftElbowTarget, tween(500))
    val normLeftHand by animateOffsetAsState(normLeftHandTarget, tween(500))

    // Eye blink animation
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        while (true) {
            delay((2000..5000).random().toLong())
            isBlinking = true
            delay(150)
            isBlinking = false
        }
    }
    
    val eyeScaleY by animateFloatAsState(
        targetValue = if (isBlinking || state == PetState.Sleeping || state == PetState.Laughing) 0.1f else 1f,
        animationSpec = tween(100),
        label = "eyeScaleY"
    )

    val bodyColor = if (state == PetState.Worried) Color(0xFFE53935) else GptAccent
    val glowColor = bodyColor.copy(alpha = 0.5f)

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onTap = { offset ->
                        val centerX = size.width / 2
                        val centerY = size.height / 2 + floatOffset
                        val radius = minOf(size.width, size.height) / 3
                        
                        val dist = Math.hypot((offset.x - centerX).toDouble(), (offset.y - centerY).toDouble())
                        if (dist < radius * 0.8) {
                            onStateChangeRequest(PetState.Laughing)
                        } else {
                            onStateChangeRequest(PetState.Happy)
                        }
                    },
                    onLongPress = {
                        onStateChangeRequest(PetState.Dizzy)
                    }
                )
            }
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2 + floatOffset
        val mainRadius = size.minDimension / 3

        // Realistic Ambient Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glowColor, Color.Transparent),
                center = Offset(centerX, centerY),
                radius = mainRadius * 1.5f * breathScale
            ),
            radius = mainRadius * 1.5f * breathScale,
            center = Offset(centerX, centerY)
        )

        // Draw Shadow beneath robot
        drawOval(
            color = Color(0x33000000),
            topLeft = Offset(centerX - mainRadius * 0.8f, centerY + mainRadius * 1.4f),
            size = Size(mainRadius * 1.6f, mainRadius * 0.3f)
        )

        // Draw Body with metallic gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(bodyColor, bodyColor.copy(alpha = 0.5f), bodyColor.copy(alpha = 0.2f)),
                center = Offset(centerX - mainRadius * 0.3f, centerY - mainRadius * 0.3f),
                radius = mainRadius * 1.5f
            ),
            radius = mainRadius,
            center = Offset(centerX, centerY)
        )
        // Add a metallic texture overlay (subtle linear gradient)
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                start = Offset(centerX - mainRadius, centerY - mainRadius),
                end = Offset(centerX + mainRadius, centerY + mainRadius)
            ),
            radius = mainRadius,
            center = Offset(centerX, centerY)
        )
        
        // Body specular highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(centerX - mainRadius * 0.4f, centerY - mainRadius * 0.4f),
                radius = mainRadius * 0.6f
            ),
            radius = mainRadius,
            center = Offset(centerX, centerY)
        )

        // Ambient rim light from bottom (bounce light)
        drawPath(
            path = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(
                    centerX - mainRadius * 0.8f, centerY + mainRadius * 0.5f,
                    centerX + mainRadius * 0.8f, centerY + mainRadius * 0.98f
                ))
            },
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color(0xFF64FFDA).copy(alpha = 0.4f)),
                startY = centerY + mainRadius * 0.5f,
                endY = centerY + mainRadius * 0.98f
            )
        )

        // Draw Screen/Face area with inner shadow look
        val faceRadius = mainRadius * 0.7f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF2E2E2E), Color(0xFF121212)),
                center = Offset(centerX, centerY - mainRadius * 0.1f),
                radius = faceRadius
            ),
            radius = faceRadius,
            center = Offset(centerX, centerY - mainRadius * 0.1f)
        )
        // Face glass highlight
        drawPath(
            path = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(
                    centerX - faceRadius * 0.6f, centerY - mainRadius * 0.1f - faceRadius * 0.8f,
                    centerX + faceRadius * 0.6f, centerY - mainRadius * 0.1f - faceRadius * 0.1f
                ))
            },
            color = Color.White.copy(alpha = 0.08f)
        )

        // Draw Eyes
        val eyeColor = if (state == PetState.Worried) Color(0xFFFF5252) else Color(0xFF64FFDA)
        val eyeSpacing = faceRadius * 0.4f
        val eyeRadius = faceRadius * 0.15f
        
        var leftEyeShape = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centerX - eyeSpacing - eyeRadius, centerY - faceRadius*0.2f - eyeRadius*eyeScaleY, centerX - eyeSpacing + eyeRadius, centerY - faceRadius*0.2f + eyeRadius*eyeScaleY)) }
        var rightEyeShape = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centerX + eyeSpacing - eyeRadius, centerY - faceRadius*0.2f - eyeRadius*eyeScaleY, centerX + eyeSpacing + eyeRadius, centerY - faceRadius*0.2f + eyeRadius*eyeScaleY)) }

        if (state == PetState.Dizzy) {
            leftEyeShape = createSpiralPath(centerX - eyeSpacing, centerY - faceRadius*0.2f, eyeRadius)
            rightEyeShape = createSpiralPath(centerX + eyeSpacing, centerY - faceRadius*0.2f, eyeRadius)
             drawPath(path = leftEyeShape, color = eyeColor, style = Stroke(width = 4f))
             drawPath(path = rightEyeShape, color = eyeColor, style = Stroke(width = 4f))
        } else if (state == PetState.Happy || state == PetState.Laughing) {
            leftEyeShape = createArchPath(centerX - eyeSpacing, centerY - faceRadius*0.2f, eyeRadius)
            rightEyeShape = createArchPath(centerX + eyeSpacing, centerY - faceRadius*0.2f, eyeRadius)
            drawPath(path = leftEyeShape, color = eyeColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
            drawPath(path = rightEyeShape, color = eyeColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
        } else if (state == PetState.Thinking) {
            leftEyeShape = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centerX - eyeSpacing - eyeRadius, centerY - faceRadius*0.2f - eyeRadius*0.2f, centerX - eyeSpacing + eyeRadius, centerY - faceRadius*0.2f + eyeRadius*0.2f)) }
            rightEyeShape = Path().apply { addOval(androidx.compose.ui.geometry.Rect(centerX + eyeSpacing - eyeRadius, centerY - faceRadius*0.2f - eyeRadius*0.2f, centerX + eyeSpacing + eyeRadius, centerY - faceRadius*0.2f + eyeRadius*0.2f)) }
            
            // Neon glow for thinking eyes
            drawPath(path = leftEyeShape, color = eyeColor.copy(alpha = 0.5f), style = Stroke(width = 12f))
            drawPath(path = rightEyeShape, color = eyeColor.copy(alpha = 0.5f), style = Stroke(width = 12f))
            drawPath(path = leftEyeShape, color = eyeColor)
            drawPath(path = rightEyeShape, color = eyeColor)
        } else {
            // Default eyes with glow
            drawPath(path = leftEyeShape, color = eyeColor.copy(alpha = 0.5f), style = Stroke(width = 12f))
            drawPath(path = rightEyeShape, color = eyeColor.copy(alpha = 0.5f), style = Stroke(width = 12f))
            drawPath(path = leftEyeShape, color = eyeColor)
            drawPath(path = rightEyeShape, color = eyeColor)
        }

        // Draw Glasses Accessory
        if (accessory == Accessory.GLASSES || accessory == Accessory.BOTH) {
            // Left lens
            drawRoundRect(
                color = Color(0x88000000),
                topLeft = Offset(centerX - eyeSpacing - eyeRadius * 1.8f, centerY - faceRadius * 0.2f - eyeRadius * 1.5f),
                size = Size(eyeRadius * 3.6f, eyeRadius * 3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(listOf(Color(0xFF666666), Color(0xFF222222))),
                topLeft = Offset(centerX - eyeSpacing - eyeRadius * 1.8f, centerY - faceRadius * 0.2f - eyeRadius * 1.5f),
                size = Size(eyeRadius * 3.6f, eyeRadius * 3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                style = Stroke(width = 6f)
            )
            // Lens reflection
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(centerX - eyeSpacing - eyeRadius * 1.2f, centerY - faceRadius * 0.2f - eyeRadius),
                end = Offset(centerX - eyeSpacing + eyeRadius * 0.8f, centerY - faceRadius * 0.2f + eyeRadius * 0.5f),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            
            // Right lens
            drawRoundRect(
                color = Color(0x88000000),
                topLeft = Offset(centerX + eyeSpacing - eyeRadius * 1.8f, centerY - faceRadius * 0.2f - eyeRadius * 1.5f),
                size = Size(eyeRadius * 3.6f, eyeRadius * 3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(listOf(Color(0xFF666666), Color(0xFF222222))),
                topLeft = Offset(centerX + eyeSpacing - eyeRadius * 1.8f, centerY - faceRadius * 0.2f - eyeRadius * 1.5f),
                size = Size(eyeRadius * 3.6f, eyeRadius * 3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                style = Stroke(width = 6f)
            )
            // Lens reflection
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(centerX + eyeSpacing - eyeRadius * 1.2f, centerY - faceRadius * 0.2f - eyeRadius),
                end = Offset(centerX + eyeSpacing + eyeRadius * 0.8f, centerY - faceRadius * 0.2f + eyeRadius * 0.5f),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
            
            // Bridge
            drawLine(
                color = Color(0xFF444444),
                start = Offset(centerX - eyeSpacing + eyeRadius * 1.8f, centerY - faceRadius * 0.2f),
                end = Offset(centerX + eyeSpacing - eyeRadius * 1.8f, centerY - faceRadius * 0.2f),
                strokeWidth = 6f
            )
        }

        // Robotic Arms
        val armWidth = mainRadius * 0.25f
        
        val rightElbow = Offset(centerX + normRightElbow.x * mainRadius, centerY + normRightElbow.y * mainRadius)
        val rightHand = Offset(centerX + normRightHand.x * mainRadius, centerY + normRightHand.y * mainRadius)
        val leftElbow = Offset(centerX + normLeftElbow.x * mainRadius, centerY + normLeftElbow.y * mainRadius)
        val leftHand = Offset(centerX + normLeftHand.x * mainRadius, centerY + normLeftHand.y * mainRadius)

        val rightShoulder = Offset(centerX + mainRadius * 0.8f, centerY + faceRadius * 0.2f)
        val leftShoulder = Offset(centerX - mainRadius * 0.8f, centerY + faceRadius * 0.2f)

        // Right arm with metallic gradient
        val rightArmPath = Path().apply {
            moveTo(rightShoulder.x, rightShoulder.y)
            lineTo(rightElbow.x, rightElbow.y)
            lineTo(rightHand.x, rightHand.y)
        }
        drawPath(rightArmPath, brush = Brush.linearGradient(listOf(bodyColor, bodyColor.copy(alpha=0.5f))), style = Stroke(width = armWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = Color(0xFF1E1E1E), radius = armWidth * 0.8f, center = rightHand)
        drawCircle(color = bodyColor, radius = armWidth * 0.8f, center = rightHand, style = Stroke(width = 4f))

        // Left arm with metallic gradient
        val leftArmPath = Path().apply {
            moveTo(leftShoulder.x, leftShoulder.y)
            lineTo(leftElbow.x, leftElbow.y)
            lineTo(leftHand.x, leftHand.y)
        }
        drawPath(leftArmPath, brush = Brush.linearGradient(listOf(bodyColor, bodyColor.copy(alpha=0.5f))), style = Stroke(width = armWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = Color(0xFF1E1E1E), radius = armWidth * 0.8f, center = leftHand)
        drawCircle(color = bodyColor, radius = armWidth * 0.8f, center = leftHand, style = Stroke(width = 4f))

        // Draw Scarf Accessory
        if (accessory == Accessory.SCARF || accessory == Accessory.BOTH) {
            val scarfY = centerY + mainRadius * 0.6f
            val scarfPath = Path().apply {
                moveTo(centerX - mainRadius * 0.8f, scarfY)
                quadraticTo(centerX, scarfY + mainRadius * 0.3f, centerX + mainRadius * 0.8f, scarfY)
                quadraticTo(centerX + mainRadius * 0.9f, scarfY + mainRadius * 0.2f, centerX + mainRadius * 0.8f, scarfY + mainRadius * 0.4f)
                quadraticTo(centerX, scarfY + mainRadius * 0.7f, centerX - mainRadius * 0.8f, scarfY + mainRadius * 0.4f)
                close()
            }
            // Scarf shadow
            drawPath(scarfPath, color = Color(0x66000000).copy(alpha = 0.3f))
            drawPath(scarfPath, brush = Brush.verticalGradient(listOf(Color(0xFFE53935), Color(0xFFB71C1C)), startY = scarfY, endY = scarfY + mainRadius * 0.7f))
            
            // Scarf tail
            val tailPath = Path().apply {
                moveTo(centerX + mainRadius * 0.5f, scarfY + mainRadius * 0.2f)
                lineTo(centerX + mainRadius * 0.7f, scarfY + mainRadius * 0.8f)
                lineTo(centerX + mainRadius * 0.4f, scarfY + mainRadius * 0.9f)
                close()
            }
            drawPath(tailPath, brush = Brush.verticalGradient(listOf(Color(0xFFD32F2F), Color(0xFF8E0000))))
        }

        // Antenna
        drawLine(
            brush = Brush.linearGradient(listOf(Color(0xFFCFD8DC), Color(0xFF78909C))),
            start = Offset(centerX, centerY - mainRadius),
            end = Offset(centerX, centerY - mainRadius * 1.4f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        // Antenna orb with glow
        val orbTint = if (battery < 15) Color(0xFFFF1744) else Color(0xFF00E5FF)
        drawCircle(
            brush = Brush.radialGradient(listOf(orbTint.copy(alpha=0.6f), Color.Transparent), center = Offset(centerX, centerY - mainRadius * 1.4f), radius = faceRadius * 0.4f),
            radius = faceRadius * 0.4f,
            center = Offset(centerX, centerY - mainRadius * 1.4f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color.White, orbTint, orbTint.copy(alpha=0.5f)), center = Offset(centerX - faceRadius*0.05f, centerY - mainRadius * 1.4f - faceRadius*0.05f), radius = faceRadius * 0.15f),
            radius = faceRadius * 0.15f * if (state == PetState.Curious) 1.5f else 1f,
            center = Offset(centerX, centerY - mainRadius * 1.4f)
        )
        
        // Mouth
        if (state == PetState.Happy || state == PetState.Celebrating) {
             val mouthPath = Path().apply {
                 moveTo(centerX - eyeSpacing*0.6f, centerY + faceRadius*0.3f)
                 quadraticTo(centerX, centerY + faceRadius*0.6f, centerX + eyeSpacing*0.6f, centerY + faceRadius*0.3f)
             }
             drawPath(path = mouthPath, color = eyeColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
        } else if (state == PetState.Worried) {
            val mouthPath = Path().apply {
                 moveTo(centerX - eyeSpacing*0.5f, centerY + faceRadius*0.4f)
                 quadraticTo(centerX, centerY + faceRadius*0.2f, centerX + eyeSpacing*0.5f, centerY + faceRadius*0.4f)
             }
             drawPath(path = mouthPath, color = eyeColor, style = Stroke(width = 6f, cap = StrokeCap.Round))
        }

        // Particle effects based on state
        if (state == PetState.Celebrating) {
            drawSparkles(centerX, centerY, mainRadius, infiniteTransition)
        }
    }
}

private fun createSpiralPath(cx: Float, cy: Float, maxRadius: Float): Path {
    val path = Path()
    var radius = 0f
    var angle = 0f
    path.moveTo(cx, cy)
    while (radius < maxRadius) {
        radius += 0.5f
        angle += 0.5f
        val x = cx + cos(angle) * radius
        val y = cy + sin(angle) * radius
        path.lineTo(x, y)
    }
    return path
}

private fun createArchPath(cx: Float, cy: Float, radius: Float): Path {
    return Path().apply {
        moveTo(cx - radius, cy)
        quadraticTo(cx, cy - radius, cx + radius, cy)
    }
}

fun DrawScope.drawSparkles(cx: Float, cy: Float, mainRadius: Float, transition: InfiniteTransition) {
    val offset1 = Offset(cx - mainRadius, cy - mainRadius)
    val offset2 = Offset(cx + mainRadius, cy - mainRadius*1.5f)
    drawCircle(Color.Yellow, radius = 5f, center = offset1)
    drawCircle(Color.White, radius = 8f, center = offset2)
}
