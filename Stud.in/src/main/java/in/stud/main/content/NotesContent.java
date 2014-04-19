package in.stud.main.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class NotesContent {

    /**
     * An array of sample (dummy) items.
     */
    public List<DummyItem> mItems = new ArrayList<DummyItem>();

    public NotesContent() {
        // Add 3 sample items.
        addItem(new DummyItem("http://some.url/image",
                "MyAwesomeNotes", "Arnav Gupta",
                "g1hjv4i1h2", new String[]{"lol", "notes"}));
        addItem(new DummyItem("http://some.url/image2",
                "MyBetterNotes", "Umair",
                "aag23fa", new String[]{"wtf", "amidoing"}));
        addItem(new DummyItem("http://some.url/image3",
                "MyGoodNotes", "Saurav",
                "as134sg", new String[]{"olala", "boing"}));
    }

    private void addItem(DummyItem item) {
        mItems.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String url;
        public String noteTitle;
        public String uploader;
        public String gcmId;
        public String[] tags;

        public DummyItem(String url, String title, String uploader, String gcmId, String[] tags) {
            this.url = url;
            this.noteTitle = title;
            this.uploader = uploader;
            this.gcmId = gcmId;
            this.tags = tags;

        }

        @Override
        public String toString() {
            return noteTitle;
        }
    }

    public int getCount () {
        return mItems.size();
    }
}
