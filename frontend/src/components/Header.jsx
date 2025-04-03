import styles from './header.module.css'

export default function (content) {
    return (
        <>
            <div className="absolute-container flex-col center-children">
                <div className={styles.headerContainer}>
                    <p className={styles.p}>THE SKWIDL PROJECT</p>
                </div>
            </div>
        </>
    );
};