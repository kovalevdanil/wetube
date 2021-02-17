
export const strip = (str, size) => {
    if (str.length < size) {
        return str
    }

    return str.substring(0, size) + '...'
}