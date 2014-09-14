(ns life.core
  (:gen-class))

(def dead-cell "0")
(def alive-cell "1")

(def north [-1 0])
(def northeast [-1 1])
(def east [0 1])
(def southeast [1 1])
(def south [1 0])
(def southwest [1 -1])
(def west [0 -1])
(def northwest [-1 -1])
(def directions [north northeast east southeast south southwest west northwest])

(defn get-row [u] (get (into [] u) 0))
(defn get-col [u] (get (into [] u) 1))

(defn add-vector
  "Adds two vectors"
  [u v]
  (map + u v))

(defn wrap
  "Wraps a position around a board."
  [board position]
  (let [wrapped-row (mod (get-row position) (count board))
        wrapped-col (mod (get-col position) (count (get board wrapped-row)))]
    [wrapped-row wrapped-col]))

(defn get-cell
  "Gets the cell at a position."
  [board position]
  (get (get board (get-row position)) (get-col position)))

(defn is-alive?
  ([cell]
   (= cell alive-cell))
  ([board
    position]
   (= (get-cell board position) alive-cell)))

(defn cell-neighbor-count
  "Gets the number of alive neighbors of a cell at a position."
  [board
   position]
  (loop [direction-index 0
         neighbor-count 0]
    (if (< direction-index (count directions))
      (let [direction (get directions direction-index)]
        (if (is-alive? board (wrap board (add-vector position direction)))
          (recur (inc direction-index) (inc neighbor-count))
          (recur (inc direction-index) neighbor-count)))
      neighbor-count)))

(defn advance-cell
  "Advances a single cell based on a given neighbor count."
  [status
   neighbor-count]
  (if (= status alive-cell)
    (if (or
          (= neighbor-count 2)
          (= neighbor-count 3))
      alive-cell
      dead-cell)
    (if (= neighbor-count 3)
      alive-cell
      dead-cell)))

(defn advance
  "Advances the board one generation."
  [board]
  (loop [row 0
         rows []]
    (if (< row (count board))
      (let [advanced-cols (loop [col 0
                                 cols []]
                            (if (< col (count (get board row)))
                              (let [position [row col]
                                    neighbor-count (cell-neighbor-count board position)
                                    status (get-cell board position)
                                    next-cell (advance-cell status neighbor-count)]
                                (recur (inc col) (conj cols next-cell)))
                              cols))]
        (recur (inc row) (conj rows advanced-cols)))
      rows)))

; Some Good Characters
; ▓
; ◯
; ◎
; ░

(defn cell-representation
  "Gets the graphical representation of a cell."
  [cell]
  (if (is-alive? cell)
    "◯ "
    "  "))

(defn board-representation
  "Returns the representation of a board."
  [board]
  (loop [representation ""
         row 0]
    (if (< row (count board))
      (let [row-representation (loop [row-representation ""
                                      col 0]
                                 (if (< col (count (get board row)))
                                   (recur (str row-representation (cell-representation (get-cell board [row col]))) (inc col))
                                   row-representation))]
        (recur (str representation row-representation "\n") (inc row)))
      representation)))

(defn prompt
  "Prompts the user for input."
  [s]
  (print s)
  (flush)
  (read-line))

(defn promptln
  "Prompts the user for input on a new line."
  [s]
  (prompt (str s "\n")))

(defn input-starting-board
  "Prompts the user to input Generation 0."
  [rows cols]
  (println "Enter Generation 0:")
  (loop [starting-board []
         r 0]
    (if (< r rows)
      (recur (conj starting-board (clojure.string/split (read-line) #""))
             (inc r))
      starting-board)))

(defn clear-screen
  "Clears the screen."
  []
  (print "\u001b[2J"))

(defn- main
  [& args]
  (let [pause-time (* 1000 (Double. (prompt "Pause between generations (seconds): ")))
        rows (Integer. (prompt "Enter Number of Rows: "))
        cols (Integer. (prompt "Enter Number of Columns: "))
        max-generation (Integer. (prompt "Enter Max Generation (-1 for infinite): "))
        starting-board (input-starting-board rows cols)]
    (loop [board starting-board
           generation 0]
      (when (or (= max-generation -1)
                (< generation max-generation))
        (clear-screen)
        (println (board-representation board))
        (println (str "-- Generation " generation " --"))
        (Thread/sleep pause-time)
        (recur (advance board) (inc generation))))))
