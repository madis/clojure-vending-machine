(ns is.mad.vending.api.user
  (:require [is.mad.vending.db :as db]
            [is.mad.vending.model :as model])
  (:refer-clojure :exclude [read]))

(defn create [{user-params :body-params :as req}]
  (let [username (:username user-params)]
    (cond
      (not (model/valid-user? user-params)) {:status 400 :body {:message "Missing fields"}}
      (db/exists? [:users username]) {:status 400 :body {:message "User already exists"}}
      :else (do
              (db/update-db [:users username] user-params)
              {:status 201 :body (db/fetch [:users username])}))))

(defn read [req]
  (let [username (get-in req [:query-params "username"])]
    (cond
      (nil? username) {:status 400 :body {:message "username URL param required"}}
      ((comp not db/exists?) [:users username]) {:status 404 :body {:message "User not found"}}
      :else {:status 200 :body (db/fetch [:users username])})))
