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

package xyz.jekyllex.ui.activities.viewer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import xyz.jekyllex.R
import xyz.jekyllex.ui.components.JekyllExAppBar
import xyz.jekyllex.ui.theme.JekyllExTheme
import xyz.jekyllex.utils.Constants.DOMAIN
import xyz.jekyllex.utils.Constants.HOME_PAGE

class WebPageViewer: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JekyllExTheme {
                var title by remember {
                    mutableStateOf(intent.getStringExtra("title") ?: "")
                }
                var currentUrl by remember {
                    mutableStateOf(intent.getStringExtra("url") ?: HOME_PAGE)
                }
                val webView = remember {
                    WebView(this).apply {
                        webViewClient = object: WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val url = request.url.toString()
                                Log.d("WebPageViewer", "Loading $url")
                                if (url.contains(DOMAIN)) return false

                                startActivity(Intent(Intent.ACTION_VIEW, request.url))
                                return true
                            }

                            override fun onPageFinished(view: WebView, url: String) {
                                currentUrl = url
                                view.title?.let { title = it }
                                super.onPageFinished(view, url)
                            }
                        }

                        loadUrl(intent.getStringExtra("url") ?: HOME_PAGE)
                    }
                }

                BackHandler {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        JekyllExAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = title,
                                        maxLines = 1,
                                        fontSize = 20.sp,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(0.dp),
                                    )
                                    Text(
                                        text = currentUrl,
                                        maxLines = 1,
                                        fontSize = 14.sp,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(0.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { this.finish() }) {
                                    Icon(
                                        contentDescription = "Go back",
                                        painter = painterResource(id = R.drawable.back),
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .size(20.dp)
                                    )
                                }
                            },
                        )
                    }
                ) { innerPadding ->
                    AndroidView(
                        factory = { webView },
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                    )
                }
            }
        }
    }
}