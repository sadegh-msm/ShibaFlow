def check_for_key(dict, keys):
    for i in range(len(keys)):
        if keys[i] not in dict.keys():
            return False
    return True
