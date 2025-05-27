package lib.kalu.mediaplayer.bean.menu;

import java.io.Serializable;
import java.util.List;

public final class Menu implements Serializable {

    private List<Item> data;

    public List<Item> getData() {
        return data;
    }

    private Menu(Menu.Builder builder) {
        data = builder.data;
    }

    public final static class Builder {
        private List<Item> data;

        public Menu.Builder setData(List<Item> v) {
            this.data = v;
            return this;
        }

        public Menu build() {
            return new Menu(this);
        }
    }

    public final static class Item implements Serializable {

        private String name;
        private boolean episode;
        private int count;
        private int freeCount;
        private int freeRes;
        private int vipRes;

        private int[] data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEpisode() {
            return episode;
        }

        public void setEpisode(boolean episode) {
            this.episode = episode;
        }

        public int[] getData() {
            return data;
        }

        public void setData(int[] data) {
            this.data = data;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getFreeCount() {
            return freeCount;
        }

        public void setFreeCount(int freeCount) {
            this.freeCount = freeCount;
        }

        public int getFreeRes() {
            return freeRes;
        }

        public void setFreeRes(int freeRes) {
            this.freeRes = freeRes;
        }

        public int getVipRes() {
            return vipRes;
        }

        public void setVipRes(int vipRes) {
            this.vipRes = vipRes;
        }
    }
}
