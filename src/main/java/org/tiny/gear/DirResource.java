package org.tiny.gear;

import wicket.markup.html.WebResource;
import wicket.util.file.File;
import wicket.util.resource.FileResourceStream;
import wicket.util.resource.IResourceStream;

/**
 *
 * @author dtmoyaji
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
