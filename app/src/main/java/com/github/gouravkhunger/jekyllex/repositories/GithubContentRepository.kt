/*
 * MIT License
 *
 * Copyright (c) 2021 Gourav Khunger
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

package com.github.gouravkhunger.jekyllex.repositories

import com.github.gouravkhunger.jekyllex.apis.github.GithubApiInstance
import com.github.gouravkhunger.jekyllex.models.CommitModel

class GithubContentRepository {
    suspend fun getRepoContent(repoName: String, path: String, accessToken: String) =
        GithubApiInstance.api.getRepoContent(repoName, path, accessToken)

    suspend fun getRawContent(repoName: String, path: String, accessToken: String) =
        GithubApiInstance.api.getRawContent(repoName, path, accessToken)

    suspend fun updateFileContent(
        commitModel: CommitModel,
        currentRepo: String,
        path: String,
        accessToken: String
    ) = GithubApiInstance.api.updateFile(commitModel, currentRepo, path, accessToken)

    suspend fun deleteFile(
        commitModel: CommitModel,
        currentRepo: String,
        path: String,
        accessToken: String
    ) = GithubApiInstance.api.deleteFile(commitModel, currentRepo, path, accessToken)
}
