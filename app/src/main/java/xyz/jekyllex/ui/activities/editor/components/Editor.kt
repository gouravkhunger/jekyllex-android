/*
 * MIT License
 *
 * Copyright (c) 2024 Gourav Khunger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.jekyllex.ui.activities.editor.components

import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import xyz.jekyllex.utils.Commands.Companion.cat
import xyz.jekyllex.utils.Constants.Companion.EDITOR_URL
import xyz.jekyllex.utils.NativeUtils
import xyz.jekyllex.utils.encodeURIComponent
import xyz.jekyllex.utils.getExtension
import xyz.jekyllex.utils.toBase64

@Composable
fun Editor(file: String) {
    val text = NativeUtils.exec(cat(file)).toBase64().encodeURIComponent()

    Log.d("Editor", "${file.getExtension()}: $text")

    Surface {
        WebView("$EDITOR_URL/?lang=${file.getExtension()}&text=$text")
    }
}
