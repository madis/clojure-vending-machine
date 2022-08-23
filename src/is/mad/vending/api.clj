(ns is.mad.vending.api
  (:require [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [spec-tools.core :as st]
            [muuntaja.core :as m]
            [is.mad.vending.model :as model]
            [is.mad.vending.api.user :as api-user]
            [is.mad.vending.api.product :as api-product]))

(def app
  (ring/ring-handler
    (ring/router
      [["/swagger.json"
        {:get {:no-doc true
               :swagger {:info {:title "Vending Machine"}}
               :basePath "/vending"
               :handler (swagger/create-swagger-handler)}}]

       ["/user" {:post {:handler api-user/create
                        :summary "Create new user"
                        :parameters {:body {:username string? :password string? :role string?}}}
                 :get {:handler api-user/read
                       :summary "Read user data"
                       :parameters {:query {:username string?}}}}]

       ["/product" {:post {:handler api-product/create
                           :summary "Add new products to sale"
                           :parameters {:body {:slot int? :code int? :quantity int? :size int? :name string? :price int?}}}
                    :get {:handler api-product/read
                          :summary "Get product details"
                          :parameters {:query {:code int?}}}
                    :delete {:handler api-product/delete
                             :summary "Remove product from sale"
                             :parameters {:body {:code int?}}}}]]

      {:exception pretty/exception
       :data {:coercion reitit.coercion.spec/coercion
              :muuntaja m/instance
              :middleware [swagger/swagger-feature
                           parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware
                           exception/exception-middleware
                           muuntaja/format-request-middleware
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware
                           multipart/multipart-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))
