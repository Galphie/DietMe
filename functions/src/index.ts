import * as functions from 'firebase-functions';

const admin = require('firebase-admin');
admin.initializeApp();

export const onFileCreated = functions.database
.ref('Archivos/users/{userId}')
.onCreate((snapshot,context) => {
    const userId = context.params.userId
    const fileId = context.params.fileId
    console.log('Archivo ' + fileId +' subido a ' + userId)

    const user = snapshot.val()
    const file = user.child().val()
    const fileName = file.name

    console.log('Nombre: ' + fileName)

})

