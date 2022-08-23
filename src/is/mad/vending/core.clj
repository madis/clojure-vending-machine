(ns is.mad.vending.core
  (:require
    [is.mad.vending.server :as vending-server])
  (:gen-class))

(defn -main [& args]
  (println "Starting vending machine api server")
  (vending-server/start-server 3939))
