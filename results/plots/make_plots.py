import csv
import matplotlib.pyplot as plt

TABLES = "../tables/"

def load(name):
    rows = []
    with open(TABLES + name) as f:
        r = csv.DictReader(f)
        for row in r:
            rows.append(row)
    return rows

# ---- workload 1 ----
rows = load("workload1_random_access.csv")
sizes = sorted(set(int(r["n"]) for r in rows))
arr_t = [int(r["avg_time_ns"]) for r in rows if r["structure"] == "DynamicArray"]
list_t = [int(r["avg_time_ns"]) for r in rows if r["structure"] == "LinkedList"]

plt.figure()
plt.plot(sizes, arr_t, marker="o", label="Dynamic Array")
plt.plot(sizes, list_t, marker="o", label="Linked List")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("avg time for 10000 get() calls (ns)")
plt.title("Workload 1: Random Access time vs n")
plt.legend()
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload1_time.png", dpi=130)

# ---- workload 2 ----
rows = load("workload2_search.csv")
arr_t = [int(r["avg_time_ns"]) for r in rows if r["structure"] == "DynamicArray"]
list_t = [int(r["avg_time_ns"]) for r in rows if r["structure"] == "LinkedList"]
arr_c = [int(r["total_comparisons"]) for r in rows if r["structure"] == "DynamicArray"]

plt.figure()
plt.plot(sizes, arr_t, marker="o", label="Dynamic Array")
plt.plot(sizes, list_t, marker="o", label="Linked List")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("avg time for 1000 contains() calls (ns)")
plt.title("Workload 2: Search time vs n")
plt.legend()
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload2_time.png", dpi=130)

plt.figure()
plt.plot(sizes, arr_c, marker="o", color="tab:green")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("total comparisons (1000 searches)")
plt.title("Workload 2: Comparisons vs n (same for both structures)")
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload2_comparisons.png", dpi=130)

# ---- workload 3 ----
rows = load("workload3_insert_remove.csv")

def series(structure, position, operation):
    return [int(r["avg_time_ns"]) for r in rows
            if r["structure"] == structure and r["position"] == position and r["operation"] == operation]

plt.figure()
plt.plot(sizes, series("DynamicArray", "start", "insert"), marker="o", label="Array insert @ start")
plt.plot(sizes, series("LinkedList", "start", "insert"), marker="o", label="List insert @ start")
plt.plot(sizes, series("DynamicArray", "middle", "insert"), marker="s", linestyle="--", label="Array insert @ middle")
plt.plot(sizes, series("LinkedList", "middle", "insert"), marker="s", linestyle="--", label="List insert @ middle")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("avg time for 1000 insertions (ns)")
plt.title("Workload 3: Insertion time vs n")
plt.legend(fontsize=8)
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload3_insert_time.png", dpi=130)

plt.figure()
plt.plot(sizes, series("DynamicArray", "start", "remove"), marker="o", label="Array remove @ start")
plt.plot(sizes, series("LinkedList", "start", "remove"), marker="o", label="List remove @ start")
plt.plot(sizes, series("DynamicArray", "middle", "remove"), marker="s", linestyle="--", label="Array remove @ middle")
plt.plot(sizes, series("LinkedList", "middle", "remove"), marker="s", linestyle="--", label="List remove @ middle")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("avg time for 1000 removals (ns)")
plt.title("Workload 3: Removal time vs n")
plt.legend(fontsize=8)
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload3_remove_time.png", dpi=130)

# ---- workload 4 ----
rows = load("workload4_heap.csv")
ins_t = [int(r["avg_insert_time_ns"]) for r in rows]
ext_t = [int(r["avg_extract_time_ns"]) for r in rows]
comp = [int(r["total_comparisons"]) for r in rows]

plt.figure()
plt.plot(sizes, ins_t, marker="o", label="n inserts, total time")
plt.plot(sizes, ext_t, marker="o", label="n extractMin, total time")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("time (ns)")
plt.title("Workload 4: Heap insert/extract time vs n")
plt.legend()
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload4_time.png", dpi=130)

plt.figure()
plt.plot(sizes, comp, marker="o", color="tab:red")
plt.xscale("log")
plt.yscale("log")
plt.xlabel("n")
plt.ylabel("total comparisons (n inserts + n extracts)")
plt.title("Workload 4: Heap comparisons vs n")
plt.grid(True, which="both", alpha=0.3)
plt.savefig("workload4_comparisons.png", dpi=130)

print("plots done")
