package com.nowcoder.community.entity;

public class Page {

    // 当前页码
    private int current = 1;        // 默认为1
    // 每页显示的条数
    private int limit = 10;         // 默认为10
    // 从数据库中查询数据总数（用于计算总页数）
    private int rows;
    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100){
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     * 页面点击页码跳转到下一页，数据库查询的时候并不是通过当前页查询，
     * 而是根据当前页的起始行查询，所以我们需要通过当前页的页面算出
     * 当前页的起始行
     * @return
     */
    public int getOffset(){
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * 这个方法是为了页面显示页码时左边界判断需要的条件
     * @return
     */
    public int getTotal(){
        // rows / limit [+1]
        if(rows % limit == 0){
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * 这个起始页码是当前页码附近的页码，左边的页码
     * 起始页码和中止页码会显示，中间的都用.省略
     * @return
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }


}
