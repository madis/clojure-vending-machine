(ns is.mad.vending.http-helpers
  (:require
    [is.mad.vending.api :as vending-api]
    [clojure.data.json :as json]
    [ring.mock.request :refer [request json-body]]))

(defn json->clj [json-string]
  (json/read-str json-string :key-fn keyword))

(defn request-api [method endpoint params]
  (let [response (-> (request method endpoint) (json-body params) (vending-api/app))
        body (-> response :body slurp json->clj)
        status (:status response)]
    {:status status :body body}))

(defn POST [endpoint params]
  (request-api :post endpoint params))

(defn GET [endpoint params]
  (request-api :get endpoint params))

(defn DELETE [endpoint params]
  (request-api :delete endpoint params))
