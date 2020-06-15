package sample;

public class ModelTableCashier{
    String id, barcode, product, qty, total;

    public ModelTableCashier(String id, String barcode, String product, String qty, String total) {
        this.id = id;
        this.barcode = barcode;
        this.product = product;
        this.qty = qty;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
