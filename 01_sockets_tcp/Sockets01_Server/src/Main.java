//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    ServidorTCP servidor = new ServidorTCP();

    // for (int i = 0; i < 2; i++ ) {
    //  servidor.execute();
    // }

    while(true) {
        servidor.execute();
    }
}
