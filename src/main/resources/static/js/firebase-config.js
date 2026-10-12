// ==============================================================
// NEXUS CAMPUS — FIREBASE CLIENT SDK INITIALIZATION & AUTH
// Connected Project: nexus-campus-33561
// ==============================================================
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/10.14.1/firebase-analytics.js";
import { 
    getAuth, 
    GoogleAuthProvider, 
    signInWithPopup, 
    signOut, 
    onAuthStateChanged 
} from "https://www.gstatic.com/firebasejs/10.14.1/firebase-auth.js";

export const firebaseConfig = {
  apiKey: "AIzaSyCmRLLDLycfGykdDKGIIopBt50hAxOv23M",
  authDomain: "nexus-campus-33561.firebaseapp.com",
  projectId: "nexus-campus-33561",
  storageBucket: "nexus-campus-33561.firebasestorage.app",
  messagingSenderId: "943652234756",
  appId: "1:943652234756:web:5c60d9b8bc02fb6124b751",
  measurementId: "G-FQPPE4QM9R"
};

export const app = initializeApp(firebaseConfig);
export const analytics = typeof window !== 'undefined' ? getAnalytics(app) : null;
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
googleProvider.setCustomParameters({ prompt: 'select_account' });

/**
 * Perform Google Authentication
 */
export async function signInWithGoogle() {
    try {
        const result = await signInWithPopup(auth, googleProvider);
        const user = result.user;
        const idToken = await user.getIdToken();
        return {
            success: true,
            user: {
                displayName: user.displayName,
                email: user.email,
                photoURL: user.photoURL,
                uid: user.uid,
                idToken: idToken
            }
        };
    } catch (error) {
        console.error("Firebase Google Auth Error:", error);
        return {
            success: false,
            code: error.code,
            message: error.message
        };
    }
}

if (typeof window !== 'undefined') {
  window.nexusFirebase = { 
      app, 
      analytics, 
      auth, 
      googleProvider, 
      signInWithGoogle, 
      signOut: () => signOut(auth),
      onAuthStateChanged: (cb) => onAuthStateChanged(auth, cb),
      config: firebaseConfig 
  };
  console.log("⚡ NEXUS CAMPUS: Connected to Firebase Project nexus-campus-33561 (Auth Active)");
}
