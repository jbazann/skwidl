"use server"
import styles from "./fast-menu-button.module.css";
import FastMenuButtonScript from "@/components/FastMenuButtonScript";
import {noneState, offBtn, offState, onBtn, onState, parentContainerState} from "@/lib/commons";
import FastMenuButtonContainer from "@/components/FastMenuButtonContainer";

const ListenersScript = FastMenuButtonScript

export default async function FastMenuButton({label, menu, id, outerId}) {
    const idFn = (str) => id + '_' + str

    return (
        <>
            <input type="checkbox" id={idFn(parentContainerState)} className={styles.displayCheck}/>
            <div className="contents">
                <input type="radio" id={idFn(noneState)} name={id + "_state_rb"} className={styles.noneRadio}/>
                <input type="radio" id={idFn(onState)} name={id + "_state_rb"} className={styles.onRadio}/>
                <input type="radio" id={idFn(offState)} name={id + "_state_rb"} className={styles.offRadio} defaultChecked/>
                <div className={styles.contentDiv}>
                    <FastMenuButtonContainer id={id} buttons={menu}/>
                </div>
                <button id={idFn(offBtn)} className={`menu-button ${styles.closeButton}`}>Back</button>
                <button id={idFn(onBtn)} className={`menu-button ${styles.openButton}`}>{label}</button>
            </div>
            <ListenersScript
                id={id}
                outerId={outerId}
            />
        </>
    )
}