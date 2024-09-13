(ns file-picker.core
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [io.github.humbleui.ui :as ui])
  (:import
    [java.io File]))

(def *dir
  (ui/signal
    (io/file (System/getProperty "user.home"))))

(def *selected
  (ui/signal nil))

(ui/defcomp file-row [file]
  [ui/clickable
   {:on-click
    (fn [event]
      (cond
        (and
          (>= (:clicks event) 2)
          (= ".." (.getName file)))
        (do
          (swap! *dir File/.getParentFile)
          (reset! *selected nil))

        (and
          (>= (:clicks event) 2)
          (.isDirectory file))
        (do
          (reset! *dir file)
          (reset! *selected nil))
        
        :else
        (reset! *selected file)))}
   (fn [state]
     [ui/rect
      {:paint {:fill (cond
                       (= file @*selected) "BBDDFF"
                       (:hovered state)    "E0F0FF"
                       :else               "FFFFFF")}}
      [ui/padding {:horizontal 8}
       [ui/row {:gap 4
                :align :center}
        [ui/size {:width 24 :height 24}
         (if (.isDirectory file)
           [ui/svg {:src "resources/folder.svg"}]
           [ui/svg {:src "resources/file.svg"}])]
        [ui/padding {:vertical 8}
         [ui/label (.getName file)]]]]])])

(ui/defcomp file-list []
  [ui/vscroll
   [ui/padding {:vertical 8}
    [ui/column
     (when (.getParentFile @*dir)
       [file-row (io/file "..")])
     (for [file (->> (.listFiles @*dir)
                  (sort-by (comp str/lower-case File/.getName)))]
       [file-row file])]]])

(ui/defcomp app []
  [ui/rect {:paint {:fill "EEE"}}
   [ui/padding {:padding 10}
    [ui/column {:gap 10}
     ^:stretch
     [ui/rect {:paint {:fill "FFF"}}
      [file-list]]
     [ui/row {:gap 10}
      [ui/align {:y :center}
       [ui/label @*dir]]
      ^:stretch [ui/gap]
      [ui/button {:style :default} "Open"]
      [ui/button "Cancel"]]]]])

(defn -main [& args]
  (ui/start-app!
    (ui/window
      {:title    "File picker"
       :mac-icon "resources/icon.icns"}
      #'app)))

(comment
  ;; Start app
  (-main)
  
  ;; View docs
  ((requiring-resolve 'io.github.humbleui.docs/open!)))
