import heapq
import time
import matplotlib.pyplot as plt

def read_file_input(f_name):
    with open(f_name, 'r') as f:
        n = int(f.readline().strip())
        coordinates = [tuple(map(int, line.split())) for line in f]
    return n, coordinates

def read_terminal_input():
    n = int(input())
    coordinates = []
    for _ in range(n):
        x, y = map(int, input().split())
        coordinates.append((x, y))
    return n, coordinates

def manhattan_dist(coord1, coord2):
    return (abs(coord1[0] - coord2[0]) + abs(coord1[1] - coord2[1]))

def get_communication_link_costs(num_coords, coordinates):
    i, j = 0, 1
    link_costs = dict()
    while i < num_coords - 1:
        cost = manhattan_dist(coordinates[i], coordinates[j])
        if i not in link_costs:
            link_costs[i] = []
        if j not in link_costs:
            link_costs[j] = []
        link_costs[i].append((cost, i, j))
        link_costs[j].append((cost, j, i))
        if j < (num_coords - 1):
            j += 1
        else:
            i += 1
            j = i + 1
    return link_costs

def MST1_heap(num_vertices, start_vertex, link_costs):
    explored, priority_queue, MST_using_heap_cost, MST_using_heap = set(), [], 0, []
    explored.add(start_vertex)
    num_explored_vertices = 1
    for link in link_costs[start_vertex]:
        heapq.heappush(priority_queue, link)
    while num_explored_vertices != num_vertices: # O(n^2 log n)
        cost, vertex_i, vertex_j = heapq.heappop(priority_queue)
        if vertex_j not in explored:
            explored.add(vertex_j)
            num_explored_vertices += 1
            MST_using_heap_cost += cost
            MST_using_heap.append((vertex_i, vertex_j, cost))
            for link in link_costs[vertex_j]: # O(n log n)
                heapq.heappush(priority_queue, link) # O(log n)
    return int(MST_using_heap_cost), MST_using_heap

def search_least_cost_edge(priority_queue_len, priority_queue, explored):
    min_cost_edge = (-1, -1, -1)
    i = 0
    while i < priority_queue_len:
        cost, vertex_i, vertex_j = priority_queue[i]
        if vertex_j not in explored:
            if min_cost_edge == (-1, -1, -1) or cost < min_cost_edge[0]:
                min_cost_edge = (cost, vertex_i, vertex_j)
        i += 1
    return min_cost_edge

def MST1_array(num_vertices, start_vertex, link_costs):
    explored, priority_queue, priority_queue_len, MST_using_arr_cost, MST_using_arr = set(), [(-1, -1, -1)] * (num_vertices ** 2), 0, 0, []
    explored.add(start_vertex)
    num_explored_vertices = 1
    for link in link_costs[start_vertex]:
        priority_queue[priority_queue_len] = link
        priority_queue_len += 1
    while num_explored_vertices != num_vertices:  # O(n^3)
        # passing in explored to optimise min_cost search - skip explored vertex
        cost, vertex_i, vertex_j = search_least_cost_edge(priority_queue_len, priority_queue, explored) # O(n^2)
        if vertex_j not in explored:
            explored.add(vertex_j)
            num_explored_vertices += 1
            MST_using_arr_cost += cost
            MST_using_arr.append((vertex_i, vertex_j, cost))
            for link in link_costs[vertex_j]:
                priority_queue[priority_queue_len] = link
                priority_queue_len += 1
            
    return int(MST_using_arr_cost), MST_using_arr

def MST2_heap(num_vertices, start_vertex, link_costs):
    MST2_heap_cost, MST2_heap = MST1_heap(num_vertices, start_vertex, link_costs)
    priority_queue = []
    optimal_MST2_cost, optimal_MST2 = -1, MST2_heap.copy()
    unexplored = set()
    # Keep removing last explored edge and see if the new resultant MST is second best - O(n^3 log n)
    while MST2_heap:
        last_explored_edge = MST2_heap[-1]
        MST2_heap_cost = MST2_heap_cost - last_explored_edge[2]
        MST2_heap.pop()

        curr_MST2, curr_MST2_cost = MST2_heap.copy(), MST2_heap_cost
        unexplored.add(last_explored_edge[1])
        explored = set(range(num_vertices)) - unexplored

        # Build new priority queue with edges to removed vertex - O(n^2 log n)
        for vertex in unexplored:
            for link in link_costs[vertex]:
                cost, vertex_i, vertex_j = link
                pq_element = (cost, vertex_j, vertex_i)
                # edge btw two unexplored vertices cannot be in priority queue when building MST - O(log n)
                if vertex_j in explored and pq_element not in priority_queue:
                    heapq.heappush(priority_queue, (cost, vertex_j, vertex_i))

        # Build new MST until exploration left - O(n^2 log n)
        while len(explored) != num_vertices:
            cost, vertex_i, vertex_j = heapq.heappop(priority_queue)
            if (vertex_i, vertex_j) != (last_explored_edge[0], last_explored_edge[1]) and vertex_i in explored and vertex_j not in explored: 
                curr_MST2_cost += cost
                curr_MST2.append((vertex_i, vertex_j, cost))
                explored.add(vertex_j)
                for link in link_costs[vertex_j]: # O(n log n)
                    if link not in priority_queue:
                        heapq.heappush(priority_queue, link)
        
        # Update optimal second MST
        if optimal_MST2_cost == -1 or curr_MST2_cost < optimal_MST2_cost:
            optimal_MST2_cost = curr_MST2_cost
            optimal_MST2 = curr_MST2

    return optimal_MST2_cost, optimal_MST2

def get_time_complexities():
    funcs = [MST1_heap, MST1_array, MST2_heap]
    func_wise_plot_vals, data_to_save_to_txt = [], [(0, 0, 0, 0, 0)] * 100
    for func in funcs:
        stations_wise_time = dict()
        for i in range(1, 101):
            # Reading from text files
            num_stations, coordinates = read_file_input(f'HW2Q3_inputs/{i}.txt')
            # Start Time
            start_time = time.time()
            link_costs = get_communication_link_costs(num_stations, coordinates)
            cost, MST_arr = func(num_stations, 0, link_costs)
            # End Time
            end_time = time.time()
            time_in_ms = (end_time - start_time) * 1000

            # Save time data for txt file
            if func == MST1_heap:
                data_to_save_to_txt[i-1] = (i, num_stations, round(time_in_ms, 2), 0, 0)
            elif func == MST1_array:
                a, b, c, _, _ = data_to_save_to_txt[i-1]
                data_to_save_to_txt[i-1] = (a, b, c, round(time_in_ms, 2), 0)
            else:
                a, b, c, d, _ = data_to_save_to_txt[i-1]
                data_to_save_to_txt[i-1] = (a, b, c, d, round(time_in_ms, 2))

            # Add time complexities
            if num_stations in stations_wise_time:
                stations_wise_time[num_stations].append(time_in_ms)
            else:
                stations_wise_time[num_stations] = [time_in_ms]
        # Getting num_stations and time complexities to plot
        x_vals, y_vals = [], []
        for num_stations in stations_wise_time.keys():
            avg_time_complexity = sum(stations_wise_time[num_stations]) / len(stations_wise_time[num_stations])
            x_vals.append(num_stations)
            y_vals.append(avg_time_complexity)
        # Sorting
        x_vals, y_vals = zip(*sorted(zip(x_vals, y_vals)))
        # Preparing array to plot everything
        func_wise_plot_vals.append((x_vals, y_vals))
    return func_wise_plot_vals, data_to_save_to_txt
 
def plot_complexity(func_wise_plot_vals):
    x_vals_heap, y_vals_heap = func_wise_plot_vals[0]
    x_vals_arr, y_vals_arr = func_wise_plot_vals[1]
    x_vals_MST2, y_vals_MST2 = func_wise_plot_vals[2]
    plt.figure(figsize=(10, 6))
    plt.plot(x_vals_heap, y_vals_heap, marker='o', color='#2E86C1', linewidth=2, markersize=4, label='MST Heap', markeredgecolor='black', markeredgewidth=1)
    plt.plot(x_vals_arr, y_vals_arr, marker='s', color='#E74C3C', linewidth=2, markersize=4, label='MST Array', markeredgecolor='black', markeredgewidth=1)
    plt.plot(x_vals_MST2, y_vals_MST2, marker='^', color='#27AE60', linewidth=2, markersize=4, label='MST 2 Heap', markeredgecolor='black', markeredgewidth=1)
    plt.xlabel('Number of Stations')
    plt.ylabel('Time Complexity (ms)')
    plt.title('Time Complexity (ms) vs Number of Stations')
    plt.legend()
    plt.grid(True, linestyle='--', alpha=0.7)
    plt.tight_layout()
    plt.savefig('time_complexity_plot.png', dpi=300, bbox_inches='tight')
    plt.show()

def get_MST1_vs_MST2_costs():
    func_wise_plot_vals, data_to_save_to_txt = [], []
    for i in range(1, 101):
        num_stations, coordinates = read_file_input(f'HW2Q3_inputs/{i}.txt')
        link_costs = get_communication_link_costs(num_stations, coordinates)
        cost_MST_1, _ = MST1_heap(num_stations, 0, link_costs)
        cost_MST_1_arr, _ = MST1_array(num_stations, 0, link_costs)
        cost_MST_2, _ = MST2_heap(num_stations, 0, link_costs)
        func_wise_plot_vals.append((cost_MST_1, cost_MST_2))
        data_to_save_to_txt.append((i, num_stations, cost_MST_1, cost_MST_1_arr, cost_MST_2))
    return func_wise_plot_vals, data_to_save_to_txt

def plot_MST_comparison(func_wise_plot_vals):
    MST1_costs, MST2_costs = zip(*func_wise_plot_vals)
    min_val, max_val = min(min(MST1_costs), min(MST2_costs)), max(max(MST1_costs), max(MST2_costs))
    padding = (max_val - min_val) * 0.05
    ax_range = [min_val - padding, max_val + padding]
    plt.figure(figsize=(10, 10))
    plt.plot(ax_range, ax_range, '--', color='gray', alpha=0.7, label='x=y')
    plt.scatter(MST1_costs, MST2_costs, alpha=0.7, color='orange', s=50, label='MST Comparison')
    plt.title('MST1 vs MST2 Cost Comparison', pad=15)
    plt.xlabel('MST1 Cost', labelpad=10)
    plt.ylabel('MST2 Cost', labelpad=10)
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.legend(framealpha=0.8)
    plt.axis('equal')
    plt.xlim(ax_range)
    plt.ylim(ax_range)
    plt.tight_layout()
    plt.savefig('mst_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()

def save_to_txt(data, output_file_name):
    with open(output_file_name, 'w') as f:
        for tuple_item in data:
            line = '\t'.join(str(x) for x in tuple_item)
            f.write(line + '\n')

if __name__ == "__main__":
    num_coords, coordinates = read_terminal_input()
    # i = 1
    # num_coords, coordinates = read_file_input(f'HW2Q3_inputs/{i}.txt')
    link_costs = get_communication_link_costs(num_coords, coordinates)
    MST_heap_cost, MST1_using_heap = MST1_heap(num_coords, 0, link_costs)
    MST_array_cost, MST1_using_array = MST1_array(num_coords, 0, link_costs)
    MST2_heap_cost, MST2_using_heap = MST2_heap(num_coords, 0, link_costs)
    print(MST_heap_cost)
    print(MST_array_cost)
    print(MST2_heap_cost)
    
    # ------------------------------------------------------------------------------
    # CODE TO CREATE PLOTS AND SAVE THE FILES - UNCOMMENT AS REQUIRED
    # ------------------------------------------------------------------------------
    # func_wise_plot_vals, time_data = get_time_complexities()
    # plot_complexity(func_wise_plot_vals)
    # MST1_vs_MST2_plot_vals, cost_data = get_MST1_vs_MST2_costs()
    # plot_MST_comparison(MST1_vs_MST2_plot_vals)
    # save_to_txt(time_data, 'time_data.txt')
    # save_to_txt(cost_data, 'cost_data.txt')
    # ------------------------------------------------------------------------------