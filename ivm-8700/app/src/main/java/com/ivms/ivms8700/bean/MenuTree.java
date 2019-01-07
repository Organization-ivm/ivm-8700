package com.ivms.ivms8700.bean;

    /**
     * 菜单树的各级菜单实体类
     *
     * @author tjs
     *
     */
    public class MenuTree {
        /** 菜单编号 */
        private String id;
        /** 菜单内容 */
        private String text;
        /** 父节点id */
        private String parentId;
        /** 是否有子节点 */
        private boolean hasChild;
        /** 层级 */
        private int level;
        /** 是否展开 */
        private boolean expanded;

        /***
         *
         * @param id
         *            菜单编号
         * @param text
         *            菜单显示内容
         * @param parentId
         *            父节点id
         * @param hasChild
         *            是否有子节点
         * @param level
         *            层级
         * @param expanded
         *            展开状态
         */
        public MenuTree(String id, String text, String parentId, boolean hasChild, int level, boolean expanded) {
            super();
            this.id = id;
            this.text = text;
            this.parentId = parentId;
            this.hasChild = hasChild;
            this.level = level;
            this.expanded = expanded;
        }

        /***
         *
         * @param id
         *            菜单编号
         * @param text
         *            菜单显示内容
         * @param parentId
         *            父节点id
         * @param hasChild
         *            是否有子节点
         * @param level
         *            层级
         */
        public MenuTree(String id, String text, String parentId, boolean hasChild, int level) {
            super();
            this.id = id;
            this.text = text;
            this.parentId = parentId;
            this.hasChild = hasChild;
            this.level = level;
            this.expanded = false;// 初始化为折叠
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public boolean isHasChild() {
            return hasChild;
        }

        public void setHasChild(boolean hasChild) {
            this.hasChild = hasChild;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public String toString() {
            return "MenuTree [id=" + id + ", text=" + text + ", parentId=" + parentId + ", hasChild=" + hasChild + ", level=" + level
                    + ", expanded=" + expanded + "]";
        }

    }
