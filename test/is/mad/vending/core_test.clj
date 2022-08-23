(ns is.mad.vending.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [is.mad.vending.api :as vending-api]
            [ring.mock.request :refer [request json-body]]
            [clojure.data.json :as json]
            [is.mad.vending.db :as db]))

(defn json->clj [json-string]
  (json/read-str json-string :key-fn keyword))

(defn POST [endpoint params]
  (-> (request :post endpoint)
      (json-body params)
      vending-api/app :body slurp json->clj))

(defn GET [endpoint params]
  (-> (request :get endpoint params)
      vending-api/app :body slurp json->clj))

(defn DELETE [endpoint params]
  (-> (request :delete endpoint)
      (json-body params)
      vending-api/app :body slurp json->clj))

(deftest endpoint-tests
  (testing "POST /user creates new user"
    (db/clean-db!)
    (let [incomplete-user {:username "Missing" :password "secret098"}
          complete-user {:username "DudeOK" :password "secret098" :role "seller"}]
      (is (= (POST "/user" incomplete-user) {:message "Missing fields"}))
      (is (= (POST "/user" complete-user) complete-user))
      (is (= (POST "/user" complete-user) {:message "User already exists"}))))

  (testing "GET /user"
    (db/clean-db!)
    (let [username "OldHat"
          user-details {:username username :password "secret007" :role "buyer"}]
      (POST "/user" user-details)
      (is (= (GET "/user" {}) {:message "username URL param required"}))
      (is (= (GET "/user" {:username "NonExistent"}) {:message "User not found"}))
      (is (= (GET "/user" {:username username}) user-details))))

  (testing "POST /product"
    (db/clean-db!)
    (is (= (POST "/product" {:slot 2 :name "Incomplete product"}) {:message "Invalid product fields"}))
    (is (= (POST "/product" {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2}) {:message "OK"})))

  (testing "GET /product"
    (db/clean-db!)
    (let [product-params {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2}]
      (POST "/product" product-params)
      (is (= (GET "/product" {:code 1}) {:message "Product not found"}))
      (is (= (GET "/product" {:code 42}) product-params))))

  (testing "DELETE /product"
    (db/clean-db!)
    (let [product-params {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2}]
      (POST "/product" product-params)
      (is (= (DELETE "/product" {:code 42}) {:message "OK"}))
      (is (= (GET "/product" {:code 42}) {:message "Product not found"})))))
