import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'
admin.initializeApp({
})

export const onUserDeleted = functions.database
    .ref('/Usuario/{userId}')
    .onDelete((snapshot, context) => {
        const userId = context.params.userId

        return snapshot.ref.root.child(`/Archivos/users/${userId}`).set(null).then.
            apply(snapshot.ref.root.child(`/Registros/${userId}`).set(null)).then.
            apply(snapshot.ref.root.child(`/Citas/users/${userId}`).set(null))

    })

export const onUserAppointmentDeleted = functions.database
    .ref('/Citas/users/{userId}/{date}')
    .onDelete((snapshot, context) => {
        const userId = context.params.userId;
        const date = context.params.date;
        const partes = date.split("-", 3);
        const year = partes[0];
        const month = partes[1];
        const day = partes[2];
        const time = snapshot.child('time').val()
        console.log(`/Citas/${year}/${month}/${day} del usuario ${userId} a las ${time}`);

        const emptyAppointment = ({
            picked: false,
            time: `${time}`,
            patientId: null,
            date: null
        });

        return snapshot.ref.root.child(`/Citas/${year}/${month}/${day}/${time}`).set(emptyAppointment)

    })
