"use client"

import {containerState, noneState, offBtn, offState, onBtn, onState, parentContainerState} from "@/lib/commons";
import {useEffect} from "react";

export default function FastMenuButtonScript({id, outerId}) {
    useEffect(() => {
        const idFn = (str) => id + '_' + str;
        const outerFn = (str) => outerId + '_' + str;

        const containerStateElem = document.querySelector('#' + idFn(parentContainerState));
        const onStateElem = document.querySelector('#' + idFn(onState));
        const offStateElem = document.querySelector('#' + idFn(offState));
        const onBtnElem = document.querySelector('#' + idFn(onBtn));
        const offBtnElem = document.querySelector('#' + idFn(offBtn));

        let outerNoneStateElem;
        let outerOnStateElem;
        let outerContainerStateElem;
        if (outerId) {
            outerNoneStateElem = document.querySelector('#' + outerFn(noneState));
            outerOnStateElem = document.querySelector('#' + outerFn(onState));
            outerContainerStateElem = document.querySelector('#' + outerFn(containerState));
        }

        if (onBtnElem) {
            onBtnElem.addEventListener("click", () => {
                if (containerStateElem) containerStateElem.checked = true;
                if (outerContainerStateElem) outerContainerStateElem.checked = true;
                if (onStateElem) onStateElem.checked = true;
                if (outerNoneStateElem) outerNoneStateElem.checked = true;
            });
        }

        if (offBtnElem) {
            offBtnElem.addEventListener("click", () => {
                if (containerStateElem) containerStateElem.checked = false;
                if (outerContainerStateElem) outerContainerStateElem.checked = false;
                if (offStateElem) offStateElem.checked = true;
                if (outerOnStateElem) outerOnStateElem.checked = true;
            });
        }

        return () => {
            onBtnElem?.removeEventListener("click", () => {});
            offBtnElem?.removeEventListener("click", () => {});
        };
    }, [id, outerId]);
}