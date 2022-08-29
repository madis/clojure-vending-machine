(ns is.mad.vending.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [is.mad.vending.http-helpers :refer [GET POST DELETE]]
            [is.mad.vending.db :as db]))

(deftest endpoint-tests
  (testing "POST /user creates new user"
    (db/clean-db!)
    (let [incomplete-user {:username "Missing" :password "secret098"}
          complete-user {:username "DudeOK" :password "secret098" :role "seller"}]
      (is (=  "Missing" (get-in (POST "/user" incomplete-user) [:body :value :username])))
      (is (= complete-user (:body (POST "/user" complete-user))))
      (is (= {:message "User already exists"} (:body (POST "/user" complete-user))))))

  (testing "GET /user"
    (db/clean-db!)
    (let [username "OldHat"
          user-details {:username username :password "secret007" :role "buyer"}]
      (POST "/user" user-details)
      (is (= 400 (:status (GET "/user" {}))))
      (is (= 404 (:status (GET "/user?username=noooo" {})) ))
      (is (= user-details (:body (GET "/user?username=OldHat" {}))))))

  (testing "POST /product"
    (db/clean-db!)
    (is (= 400 (:status (POST "/product" {:slot 2 :name "Incomplete product"}))))
    (is (= {:message "OK"} (:body (POST "/product" {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2})))))

  (testing "GET /product"
    (db/clean-db!)
    (let [product-params {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2}]
      (POST "/product" product-params)
      (is {:message "Product not found"} (:body (= (GET "/product?product=1" {}))))
      (is (= product-params (:body (GET "/product?code=42" {}))))))

  (testing "DELETE /product"
    (db/clean-db!)
    (let [product-params {:slot 1 :code 42 :quantity 2 :size 2 :name "Water" :price 2}]
      (POST "/product" product-params)
      (is (= {:message "OK"} (:body (DELETE "/product" {:code 42}))))
      (is (= {:message "Product not found"} (:body (GET "/product?code=42" {})) )))))
