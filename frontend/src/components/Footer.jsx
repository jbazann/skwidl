import styles from './footer.module.css'

export default function () {
    return (
        <div className={`${styles.footingContainer} relative-container flex-col`}>
            <p className={`${styles.footingText} center-self`}>contact: mail@jbazann.dev</p> {/* TODO overflow, dynamic fetch, etc etc etc*/}
        </div>
    );
};