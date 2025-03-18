package com.android.calculator


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.calculator.ui.theme.计算器Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // 启用全屏模式
        setContent {
            计算器Theme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var input by remember { mutableStateOf("") } // 存储用户输入
    var result by remember { mutableStateOf("0") } // 存储计算结果

    Column(
        modifier = Modifier
            .fillMaxSize().padding(bottom = 5.dp, top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

    ) {
        val scrollState = rememberScrollState() // 创建滚动状态
        Column(
            modifier = Modifier.weight(1.0f)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(end = 20.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ){
            Text(
                text = input.ifEmpty { "0" },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp),
                lineHeight = 40.sp
            )
            Text(
                text = "$result",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                lineHeight = 72.sp
            )
        }


        Spacer(modifier = Modifier.height(16.dp))


        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 第一行：C, ±, %, ÷
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CalculatorButton("C", Color(0xfffa5252), Modifier.weight(1f),textColor = Color.White) {
                    input = ""; result = "0"
                }
                CalculatorButton("±", Color(0xff495057), Modifier.weight(1f) ,textColor = Color.White) {
                    input = if (input.startsWith("-")) input.drop(1) else "-$input"
                }
                CalculatorButton("%", Color(0xff495057), Modifier.weight(1f) ,textColor = Color.White) {
                    input = (input.toDoubleOrNull()?.div(100) ?: 0.0).toString()
                }
                CalculatorButton("÷", Color(0xfffab005), Modifier.weight(1f) ,textColor = Color.White) {
                    input += " ÷ "
                }
            }

            // 第二行：7, 8, 9, ×
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CalculatorButton("7", modifier = Modifier.weight(1f)) { input += "7" }
                CalculatorButton("8", modifier = Modifier.weight(1f)) { input += "8" }
                CalculatorButton("9", modifier = Modifier.weight(1f)) { input += "9" }
                CalculatorButton("×", Color(0xfffab005), Modifier.weight(1f), textColor = Color.White) { input += " × " }
            }

            // 第三行：4, 5, 6, -
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CalculatorButton("4", modifier = Modifier.weight(1f)) { input += "4" }
                CalculatorButton("5", modifier = Modifier.weight(1f)) { input += "5" }
                CalculatorButton("6", modifier = Modifier.weight(1f)) { input += "6" }
                CalculatorButton("-", Color(0xfffab005), Modifier.weight(1f), textColor = Color.White) { input += " - " }
            }

            // 第四行：1, 2, 3, +
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CalculatorButton("1", modifier = Modifier.weight(1f)) { input += "1" }
                CalculatorButton("2", modifier = Modifier.weight(1f)) { input += "2" }
                CalculatorButton("3", modifier = Modifier.weight(1f)) { input += "3" }
                CalculatorButton("+", Color(0xfffab005), Modifier.weight(1f), textColor = Color.White) { input += " + " }
            }

            // 第五行：0, ., =
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Calculator0Button("0", modifier = Modifier.weight(2.06f)) { input += "0" } // 0 按钮占据两倍宽度
                CalculatorButton(".", modifier = Modifier.weight(1f)) { input += "." }
                CalculatorButton("=", Color.Black, textColor = Color.White, modifier = Modifier.weight(1f)) {
                    try {
                        val expression = input.replace(" ", "")
                        val calculatedResult = evaluateExpression(expression)
                        result = formatResult(calculatedResult.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        result = "Error"
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    color: Color = Color.LightGray,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
             .aspectRatio(1f)
            .padding(4.dp)
    ) {
        Text(text = text, fontSize = 30.sp, color = textColor)
    }
}

@Composable
fun Calculator0Button(
    text: String,
    color: Color = Color.LightGray,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier
            .aspectRatio(2f)
            .height(64.dp)
            .padding(4.dp)
    ) {
        Text(text = text, fontSize = 30.sp, color = textColor)
    }
}

// 计算表达式的函数
fun evaluateExpression(expression: String): Double {
    return object : Any() {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < expression.length) expression[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < expression.length) throw RuntimeException("Unexpected: ${ch.toChar()}")
            return x
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('×'.code) -> x *= parseFactor()
                    eat('÷'.code) -> x /= parseFactor()
                    else -> return x
                }
            }
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()
            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                x = expression.substring(startPos, pos).toDouble()
            } else {
                throw RuntimeException("Unexpected: ${ch.toChar()}")
            }
            return x
        }
    }.parse()
}

// 格式化结果的函数
fun formatResult(result: String): String {
    return try {
        val number = result.toDouble()
        if (number == number.toInt().toDouble()) {
            number.toInt().toString() // 如果是整数，去掉小数部分
        } else {
            result // 如果是小数，保留小数部分
        }
    } catch (e: Exception) {
        result // 如果解析失败，返回原结果
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    计算器Theme {
        CalculatorApp()
    }
}