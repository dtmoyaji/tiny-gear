/*
 * Copyright 2023 bythe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tiny.gear;

import wicket.markup.html.WebResource;
import wicket.util.file.File;
import wicket.util.resource.FileResourceStream;
import wicket.util.resource.IResourceStream;

/**
 *
 * @author bythe
 */
public class DirResource extends WebResource {

    public static final long serialVersionUID = -1L;

    private final File basedir;

    public DirResource(File basedir) {
        this.basedir = basedir;
    }

    @Override
    public IResourceStream getResourceStream() {
        String fileName = getParameters().getString("file");
        File file = new File(basedir, fileName);
        return new FileResourceStream(file);
    }
}
