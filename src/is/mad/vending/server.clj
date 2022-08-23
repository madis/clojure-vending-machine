(ns is.mad.vending.server
  (:require [org.httpkit.server :as server]
            [ring.middleware.reload :refer [wrap-reload]]
            [is.mad.vending.api :as vending-api])
  (:gen-class))

(defonce ^:private http-server (atom nil))

(defn start-server [port]
  (reset! http-server (server/run-server (wrap-reload #'vending-api/app) {:port port})))

(defn stop-server
  "Gracefully shut down the server, waiting 100 ms"
  []
  (when-not (nil? @http-server)
    (println "Gracefully stopping api-server")
    (@http-server :timeout 100)
    (reset! http-server nil)
    (println "Done stopping api-server")))
