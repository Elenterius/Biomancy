# # write edgelist to grid.edgelist
# nx.write_edgelist(DG, path="grid.edgelist", delimiter=":")
# # read edgelist from grid.edgelist
# H= nx.read_edgelist(path="grid.edgelist", delimiter=":")

import json
import matplotlib.image as mpimg
import matplotlib.pyplot as plt
import networkx as nx
import os
import subprocess
import time
import zipfile

os.environ["PATH"] += os.pathsep + 'D:/Program Files/Graphviz/bin'  # workaround


def get_ingredients_with_amount(ingredient_list):
    ingredients = {}
    for ingredient in ingredient_list:
        key = 'item'
        if key in ingredient:
            item = ingredient['item']
        else:
            item = "tag=" + ingredient['tag']

        if item not in ingredients:
            ingredients[item] = 1
        else:
            ingredients[item] += 1

    return ingredients


def add_digester_recipe(recipe, output_node: str, digraph: nx.Graph, labels: dict, inter_connect: bool):
    recipe_hash = str(hash(output_node)) if not inter_connect else ""

    # output nodes ####################################################
    output_item = recipe_hash + recipe['result']['item'].replace(":", "_")
    if 'count' in recipe['result']:
        amount = recipe['result']['count']
    else:
        amount = 1
    digraph.add_edge(output_node, output_item, weight=amount)
    labels[output_item] = recipe['result']['item']
    labels[output_node] = f'digester, {recipe["time"]} ticks'

    byproduct = recipe['byproduct']
    item = recipe_hash + byproduct['result']['item'].replace(":", "_")
    chance = byproduct['chance']
    digraph.add_edge(output_node, item, weight=chance)
    labels[item] = byproduct['result']['item']

    # input node ######################################################
    ingredient = recipe['ingredient']
    if 'item' in ingredient:
        ingredient = ingredient['item']
    else:
        ingredient = "tag=" + ingredient['tag']
    node = recipe_hash + ingredient.replace(":", "_")
    digraph.add_edge(node, output_node, weight=1)
    labels[node] = ingredient


def add_chewer_recipe(recipe, output_node: str, digraph: nx.Graph, labels: dict, inter_connect: bool):
    recipe_hash = str(hash(output_node)) if not inter_connect else ""

    # output node ####################################################
    output_item = recipe_hash + recipe['result']['item'].replace(":", "_")
    if 'count' in recipe['result']:
        amount = recipe['result']['count']
    else:
        amount = 1
    digraph.add_edge(output_node, output_item, weight=amount)
    labels[output_item] = recipe['result']['item']
    labels[output_node] = f'chewer, {recipe["time"]} ticks'

    # input node ######################################################
    ingredient = recipe['ingredient']
    if 'item' in ingredient:
        ingredient = ingredient['item']
    else:
        ingredient = "tag=" + ingredient['tag']
    node = recipe_hash + ingredient.replace(":", "_")
    digraph.add_edge(node, output_node, weight=1)
    labels[node] = ingredient


def add_evolution_pool_recipe(recipe, output_node: str, digraph: nx.Graph, labels: dict, inter_connect: bool):
    recipe_hash = str(hash(output_node)) if not inter_connect else ""

    # output nodes ####################################################
    output_item = recipe_hash + recipe['result']['item'].replace(":", "_")
    if 'count' in recipe['result']:
        amount = recipe['result']['count']
    else:
        amount = 1
    digraph.add_edge(output_node, output_item, weight=amount)
    labels[output_item] = recipe['result']['item']
    labels[output_node] = f'evolution pool, {recipe["time"]} ticks'

    # input nodes ######################################################
    ingredients = get_ingredients_with_amount(recipe['ingredients'])
    for ingredient in ingredients:
        amount = ingredients[ingredient]
        node = recipe_hash + ingredient.replace(":", "_")
        digraph.add_edge(node, output_node, weight=amount)
        labels[node] = ingredient


def add_decomposer_recipe(recipe, output_node: str, digraph: nx.Graph, labels: dict, inter_connect: bool):
    recipe_hash = str(hash(output_node)) if not inter_connect else ""

    # output nodes ####################################################
    output_item = recipe_hash + recipe['result']['item'].replace(":", "_")
    if 'count' in recipe['result']:
        amount = recipe['result']['count']
    else:
        amount = 1
    digraph.add_edge(output_node, output_item, weight=amount)
    labels[output_item] = recipe['result']['item']
    labels[output_node] = f'decomposer, {recipe["time"]} ticks'

    for byproduct in recipe['byproducts']:
        item = recipe_hash + byproduct['result']['item'].replace(":", "_")
        chance = byproduct['chance']
        digraph.add_edge(output_node, item, weight=chance)
        labels[item] = byproduct['result']['item']

    # input nodes ######################################################
    ingredients = get_ingredients_with_amount(recipe['ingredients'])
    for ingredient in ingredients:
        amount = ingredients[ingredient]
        node = recipe_hash + ingredient.replace(":", "_")
        digraph.add_edge(node, output_node, weight=amount)
        labels[node] = ingredient


def get_node_textures(labels: dict):
    biomancy_assets_dir = "../src/main/resources/assets/biomancy"
    cached_textures = dict()
    textures = dict()

    for key in labels:
        label = labels[key]
        if label.startswith("tag="):
            continue

        if label in cached_textures:
            if cached_textures[label] is not None:
                textures[key] = cached_textures[label]
        else:
            if ":" not in label:
                continue

            namespace, item_name = label.split(":")

            if namespace == "biomancy":
                model = f'{biomancy_assets_dir}/models/item/{item_name}.json'
            else:
                model = f'./lib/assets/{namespace}/models/item/{item_name}.json'

            if not os.path.exists(model):
                cached_textures[label] = None
                print("WARN: missing item model: " + model)
                continue

            with open(model, "rb") as f:
                item_model = json.loads(f.read())

            if "textures" in item_model:
                textures[key] = []
                cached_textures[label] = []

                for texture_layer in item_model["textures"]:
                    texture_id = item_model["textures"][texture_layer]  # layer0, etc

                    if ":" in texture_id:
                        namespace, texture_path = texture_id.split(":")
                    else:
                        namespace = "minecraft"
                        texture_path = texture_id

                    if namespace == "biomancy":
                        item_texture = f'{biomancy_assets_dir}/textures/{texture_path}.png'
                    else:
                        item_texture = f'./lib/assets/{namespace}/textures/{texture_path}.png'

                    if not os.path.exists(item_texture):
                        print("WARN: missing item texture: " + item_texture)
                        continue

                    texture_data = mpimg.imread(item_texture)
                    textures[key].append(texture_data)
                    cached_textures[label].append(texture_data)
            else:
                print("WARN: texture not loaded: " + label)
                # TODO: get texture for block

    return textures


def draw_recipe_digraph(figsize=(40, 40), prog="dot", inter_connect=False):
    recipes_dir = "../src/generated/resources/data/biomancy/recipes"
    (path, _, filenames) = next(os.walk(recipes_dir))

    DG = nx.DiGraph()
    node_labels = dict()

    print("parsing recipes and building digraph...")
    for name in filenames:
        f_in = f'{path}/{name}'
        name = name.split(".")[0]

        with open(f_in, "rb") as f:
            recipe_json = json.loads(f.read())

        # print(recipe_json, "\n")
        if recipe_json['type'] == 'biomancy:decomposing':
            add_decomposer_recipe(recipe_json, name, DG, node_labels, inter_connect)
        elif recipe_json['type'] == 'biomancy:chewing':
            add_chewer_recipe(recipe_json, name, DG, node_labels, inter_connect)
        elif recipe_json['type'] == 'biomancy:digesting':
            add_digester_recipe(recipe_json, name, DG, node_labels, inter_connect)
        elif recipe_json['type'] == 'biomancy:evolution_pool':
            add_evolution_pool_recipe(recipe_json, name, DG, node_labels, inter_connect)

    print("loading textures...")
    node_textures = get_node_textures(node_labels)

    edge_labels = dict([((n1, n2), d) for n1, n2, d in DG.edges.data("weight", default=1)])

    fig = plt.gcf()
    fig.set_size_inches(figsize[0], figsize[1])

    print("layouting digraph...", end='')
    start_time = time.time()
    pos = nx.nx_pydot.pydot_layout(DG, prog=prog)
    print(f" {time.time() - start_time}s")

    print("drawing digraph...")
    nx.draw(DG, pos, arrowsize=20)

    print(" - drawing labels...", end='')
    start_time = time.time()
    nx.draw_networkx_edge_labels(DG, pos, edge_labels=edge_labels, label_pos=0.65, font_size=14)
    for key in node_labels:
        node_labels[key] = "     " + node_labels[key]
    nx.draw_networkx_labels(DG, pos, labels=node_labels, font_size=12, horizontalalignment="left")
    print(f" {time.time() - start_time}s")

    print(" - drawing textures...", end='')
    start_time = time.time()
    ax = plt.gca()
    trans = ax.transData.transform
    trans2 = fig.transFigure.inverted().transform
    img_size = 0.01
    for n in DG.nodes():
        if n in node_textures:
            (x, y) = pos[n]
            xx, yy = trans((x, y))  # figure coordinates
            xa, ya = trans2((xx, yy))  # axes coordinates
            for texture in node_textures[n]:
                a = plt.axes([xa - img_size / 2.0, ya - img_size / 2.0, img_size, img_size])
                a.imshow(texture)
                a.set_aspect('equal')
                a.axis('off')
    print(f" {time.time() - start_time}s")

    print("exporting drawing to pdf...")
    fig.savefig(f'biomancy_production_digraph_{prog}.pdf')

    # https://cytoscape.org/ or https://www.yworks.com/products/yed
    print("exporting graph to GLM...")
    nx.readwrite.gml.write_gml(DG, 'biomancy_production_digraph.gml')


def get_mc_assets():
    print("checking for minecraft assets...")
    mc_jar = "./lib/client-extra.jar"
    mc_assets_dir = "./lib/assets/minecraft"

    if not os.path.exists(mc_assets_dir):
        print("minecraft assets folder is missing!")

        if not os.path.exists(mc_jar):
            print("starting gradle...")
            target_dir = os.path.abspath('..')
            p = subprocess.Popen(["gradlew", "-q", "copyMCClientJar"], cwd=target_dir, shell=True)
            p.wait()
            print("...done!")

        print("unzipping minecraft assets...")
        jar = zipfile.ZipFile(mc_jar)
        for name in jar.namelist():
            if name.startswith('assets/minecraft/textures') or name.startswith('assets/minecraft/models'):
                jar.extract(name, "./lib")


if __name__ == '__main__':
    get_mc_assets()
    draw_recipe_digraph(figsize=(75, 75), prog="twopi", inter_connect=True)  # dot, neato, twopi
