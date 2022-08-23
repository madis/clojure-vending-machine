(ns is.mad.vending.api.product
  (:require [is.mad.vending.db :as db]
            [is.mad.vending.model :as model])
  (:refer-clojure :exclude [read]))

(defn str->int [s]
  (if (int? s)
    s
    (try
      (Integer/parseInt s)
      (catch NumberFormatException e 0))))

(defn store-item [item]
  (let [code (:code item)
        current-item (db/fetch [:products code])
        added-quantity (str->int (:quantity item))
        current-quantity (or (str->int (:quantity current-item)) 0)
        updated-quantity (+ current-quantity added-quantity)
        updated-item (merge current-item item {:quantity updated-quantity})]
    (db/update-db [:products code] updated-item)))

(defn create [req]
  (let [product-params (:body-params req)]
    (cond
      (not (model/valid-product? product-params)) {:status 400 :body {:message "Invalid product fields"}}
      :else (do
              (store-item product-params)
              {:status 201 :body {:message "OK"}}))))

(defn read [req]
  (let [code (str->int (get-in req [:query-params "code"]))]
    (cond
      (nil? code) {:status 400 :body {:message "code URL param required"}}
      (not (db/exists? [:products code])) {:status 404 :body {:message "Product not found"}}
      :else {:status 200 :body (db/fetch [:products code])})))

(defn delete [req]
  (let [code (get-in req [:body-params :code])]
    (db/update-db [:products code] nil)
    {:status 200 :body {:message "OK"}}))
