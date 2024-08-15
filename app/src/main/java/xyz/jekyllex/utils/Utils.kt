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

package xyz.jekyllex.utils

import java.io.File
import android.content.Context
import xyz.jekyllex.utils.Commands.bundle
import xyz.jekyllex.utils.Commands.git
import xyz.jekyllex.utils.Commands.jekyll

private val denyList = arrayOf("ls", "ln", "cd")
fun Array<String>.isDenied(): Boolean = this.any { it in denyList }
fun Array<String>.drop(n: Int): Array<String> = this.toList().drop(n).toTypedArray()

fun Array<String>.transform(context: Context): Array<String> = this.let {
    val settings = Settings(context)
    when (this.getOrNull(0)) {
        "bundle" -> {
            val localGems = settings.get<Boolean>(Setting.LOCAL_GEMS)
            if (localGems && this.any {
                    it in arrayOf("install", "update")
                }) {
                bundle(*this.drop(1), "--prefer-local")
            } else this
        }

        "git" -> {
            val enableProgress = settings.get<Boolean>(Setting.LOG_PROGRESS)
            if (enableProgress && this.any {
                    it in arrayOf("clone", "fetch", "pull", "push")
                }) {
                git(true, *this.drop(1))
            } else this
        }

        "jekyll" -> {
            if (this.getOrNull(1) == "new") {
                val skipBundle = settings.get<Boolean>(Setting.SKIP_BUNDLER)
                if (skipBundle) jekyll(*this.drop(1), "--skip-bundle")
                else this
            }
            else if (this.getOrNull(1) == "serve" && this.size == 2) {
                val liveReload = settings.get<Boolean>(Setting.LIVERELOAD)
                val prefixBundler = settings.get<Boolean>(Setting.PREFIX_BUNDLER)
                val flags = settings.get<String>(Setting.JEKYLL_FLAGS).split(" ")

                val command = prefixBundler.let {
                    if (it) bundle("exec", *jekyll("serve"))
                    else jekyll("serve")
                }.toMutableList()

                if (liveReload) command.add("-l")
                command.addAll(flags)

                command.toTypedArray()
            }
            else this
        }

        else -> this
    }
}

fun File.removeSymlinks() {
    if (this.isDirectory) {
        this.listFiles()?.forEach { it.removeSymlinks() }
    } else {
        if (this.canonicalPath != this.absolutePath) {
            this.apply {
                val text = readText()
                delete()
                createNewFile()
                writeText(text)
            }
        }
    }
}
