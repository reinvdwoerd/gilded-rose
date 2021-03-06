(ns products)


(defn within-bounds [n]
  (cond
    (<= n 0)  0
    (>= n 50) 50
    :else     n))

(defn age-normally [{:keys [sell-in quality name]} & [multiplier]]
  (assert sell-in (str "Normal aging products need a 'sell-in' key"))
  (if (< sell-in 0)
    (- quality (* 2 (or multiplier 1)))
    (- quality (* 1 (or multiplier 1)))))

(defmulti update-quality :type)

(defmethod update-quality :default [this]
  (within-bounds (age-normally this)))

(defmethod update-quality :conjured [this]
  (within-bounds (age-normally this 2)))

(defmethod update-quality :ripening [{:keys [quality]}]
  (within-bounds (+ quality 2)))

(defmethod update-quality :legendary [{:keys [quality]}]
  quality)

(defmethod update-quality :ticket [{:keys [sell-in quality]}]
  (within-bounds
    (condp >= sell-in
      0  0
      5  (+ quality 3)
      10 (+ quality 2)
         (+ quality 1))))

; Exported
(defn product
 [name & {:keys [quality sell-in type]
          :as product
          :or {:quality 50 :type :default}}]
 (assoc product :name name))

(defn update-product [{:keys [sell-in quality] :as product}]
 (as-> product p
   (if (some? (:sell-in p))
     (update p :sell-in dec) p)
   (assoc p :quality
     (update-quality p))))
