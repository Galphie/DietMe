import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'
admin.initializeApp({
})

export const onUserDeleted = functions.database
.ref('/Usuario/{userId}')
.onDelete((snapshot,context) => {
    const userId = context.params.userId

    return snapshot.ref.root.child('/Archivos/users/' + userId).set(null).then.
    apply(snapshot.ref.root.child('Registros/' + userId).set(null))
    
})