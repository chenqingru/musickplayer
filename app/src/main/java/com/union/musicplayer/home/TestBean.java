package com.union.musicplayer.home;

import java.util.List;

public class TestBean {

    public List<BookData> data;
    public class BookData{
        String author;
        List<Children> children;
    }



    class Children {
        String author;
        public int courseId;
        public int id;
        public String name;
        public int order;
        public int parentChapterId;
        public int visible;
    }

}
