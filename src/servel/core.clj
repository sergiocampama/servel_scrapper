(ns servel.core
  (:require [org.httpkit.client :as http]
    [clojure.data.json :as json]
    [monger.core :as mg]
    [monger.collection :as mgcoll]
    [chileno.dv :as dv])
  (:import [com.mongodb MongoOptions ServerAddress]))

(defn info-for-body [{name "NOMBRE" vote_venue "LOCVOTACION" table "NMESA" 
                      county "NCOMUNA" enabled "HABILITADO" vote_venue_address "DIRLOCVOTACION" 
                      province "NPROVINCIA" region "NOREGION" vocal "VOCAL" rut "RUT" dv "DV"}]
  { :name name, :vote_venue vote_venue, :table table, :vote_venue_address vote_venue_address, 
    :county county, :province province, :region region, :rut (str rut "-" dv)
    :enabled (= enabled 0) :vocal (not= vocal 0)})

(defn get-info-for-rut [rut]
  (let [options {:timeout 20000 :keep-alive 3000 :form-params { "run" rut "dv" (dv/make (str rut))}}]
    @(http/post "http://www.servel.cl/ConsultaDatosElectorales/CdeConsultaDatosElectorales" options 
      (fn [{body :body}]
        (info-for-body (json/read-str body))))))

(def INTERVAL_SIZE 10000)

(defn scrape []
  (println "Scraping...")
  (doseq [i (range 0 3000)]
    (time (let [start (* i INTERVAL_SIZE) stop (dec (* (inc i) INTERVAL_SIZE))]
      (println (str "Downloading from " start " to " stop))
      (mgcoll/insert-batch "person" (doall (pmap get-info-for-rut (range start stop))))))))

(defn -main [& args]
  (mg/connect!)
  (mg/set-db! (mg/get-db "servel"))
  (println "Servel Webscrapper: Start")
  (scrape)
  (mg/disconnect!)
  (println "Stop"))
