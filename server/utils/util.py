from model import user, music, music_interaction


def check_for_key(dict, keys):
    for i in range(len(keys)):
        if keys[i] not in dict.keys():
            return False
    return True


def like_or_dislike_music(user_id, music_id, status):
    if status == 'like':
        music_interaction.like_music(user_id, music_id)
        return True
    elif status == 'dislike':
        music_interaction.dislike_music(user_id, music_id)
        return True
    else:
        return False
