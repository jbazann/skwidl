import styles from "./content-area.module.css";

export default function ({header, mainContent, footer}) {
    return (
        <div className={styles.contentArea}>
            <header className={`relative-container`}>
                <div className={`absolute-container ${styles.headerBorderContainer}`}>
                    <div className={`hr`}></div>
                </div>
                {header}
            </header>
            <main id="main-content-container" className={`relative-container`}>
                {mainContent}
            </main>
            <footer className={`relative-container`}>
                <div className={`absolute-container ${styles.footerBorderContainer}`}>
                    <div className={`hr`}></div>
                </div>
                {footer}
            </footer>
        </div>
    );
};