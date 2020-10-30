import java.util.List;

public class OrderList {
    private int totalItemNum;
    private List<Item> pageItems;

    public int getTotalItemNum() {
        return totalItemNum;
    }

    public void setTotalItemNum(int totalItemNum) {
        this.totalItemNum = totalItemNum;
    }

    public List<Item> getPageItems() {
        return pageItems;
    }

    public void setPageItems(List<Item> pageItems) {
        this.pageItems = pageItems;
    }

    class Item {
        //商品信息
        private String goods_name;
        // 买家
        private String nickname;
        //订单号
        private String order_sn;
        //订单/售后状态
        private String order_status_str;
        // 收货人
        private String receive_name;
        //创建时间
        private String confirm_time;
        //承诺发货时间
        private String promise_shipping_time;
        // 具体商品
        private String spec;
        //数量
        private int goods_number;
        // 商品总价
        private int goods_amount;
        // id
        private long goods_id;

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOrder_sn() {
            return order_sn;
        }

        public void setOrder_sn(String order_sn) {
            this.order_sn = order_sn;
        }

        public String getOrder_status_str() {
            return order_status_str;
        }

        public void setOrder_status_str(String order_status_str) {
            this.order_status_str = order_status_str;
        }

        public String getReceive_name() {
            return receive_name;
        }

        public void setReceive_name(String receive_name) {
            this.receive_name = receive_name;
        }

        public String getConfirm_time() {
            return confirm_time;
        }

        public void setConfirm_time(String confirm_time) {
            this.confirm_time = confirm_time;
        }

        public String getPromise_shipping_time() {
            return promise_shipping_time;
        }

        public void setPromise_shipping_time(String promise_shipping_time) {
            this.promise_shipping_time = promise_shipping_time;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public int getGoods_number() {
            return goods_number;
        }

        public void setGoods_number(int goods_number) {
            this.goods_number = goods_number;
        }

        public int getGoods_amount() {
            return goods_amount;
        }

        public void setGoods_amount(int goods_amount) {
            this.goods_amount = goods_amount;
        }

        public long getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(long goods_id) {
            this.goods_id = goods_id;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "goods_name='" + goods_name + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", order_sn='" + order_sn + '\'' +
                    ", order_status_str='" + order_status_str + '\'' +
                    ", receive_name='" + receive_name + '\'' +
                    ", confirm_time='" + confirm_time + '\'' +
                    ", promise_shipping_time='" + promise_shipping_time + '\'' +
                    ", spec='" + spec + '\'' +
                    ", goods_number=" + goods_number +
                    ", goods_amount=" + goods_amount +
                    ", goods_id=" + goods_id +
                    '}';
        }
    }
}
