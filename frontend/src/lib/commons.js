import { randomUUID } from 'crypto'

let previousIds = new Map()
export async function identifier(key = '') {
    return key ?
        (previousIds.has(key) ?
            previousIds :
            previousIds.set(key,_identifier())).get(key) :
        _identifier()
}

function _identifier() {
    return 'i' + randomUUID().replace(/-/g,'')
}

// TODO come up with better name for these hehe
export const onBtn = 'on_button'
export const offBtn = 'off_button'
export const onState = 'on_state'
export const offState =  'off_state'
export const noneState = 'none_state'
export const containerState = 'container_state'
export const parentContainerState = 'parent_container_state'
